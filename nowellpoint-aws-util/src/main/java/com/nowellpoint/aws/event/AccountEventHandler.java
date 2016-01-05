package com.nowellpoint.aws.event;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.data.User;
import com.nowellpoint.aws.model.idp.Account;
import com.nowellpoint.aws.model.idp.CreateAccountRequest;
import com.nowellpoint.aws.model.idp.CreateAccountResponse;
import com.nowellpoint.aws.model.idp.SearchAccountRequest;
import com.nowellpoint.aws.model.idp.SearchAccountResponse;
import com.nowellpoint.aws.model.idp.UpdateAccountRequest;
import com.nowellpoint.aws.model.idp.UpdateAccountResponse;
import com.nowellpoint.aws.provider.ConfigurationProvider;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class AccountEventHandler implements AbstractEventHandler {

	@Override
	public void process(Event event, Context context) throws IOException {
		
		LambdaLogger logger = context.getLogger();
		
		logger.log(new Date() + " starting AccountEventHandler");
		
		//
		// parse the event payload
		//
		
		Account account = objectMapper.readValue(event.getPayload(), Account.class);
		
		//
		// setup IdentityProviderClient
		//
		
		final IdentityProviderClient identityProviderClient = new IdentityProviderClient();
		
		//
		// search for exsiting account with username
		//
		
		SearchAccountRequest searchAccountRequest = new SearchAccountRequest().withApiKeyId(ConfigurationProvider.getStormpathApiKeyId())
				.withApiKeySecret(ConfigurationProvider.getStormpathApiKeySecret())
				.withUsername(account.getUsername());
		
		SearchAccountResponse searchAccountResponse = identityProviderClient.search(searchAccountRequest);
		
		logger.log(new Date() + " found: " + searchAccountResponse.getSize());
		
		String href;
		
		if (searchAccountResponse.getSize() == 0) {
			
			//
			// build the CreateAccountRequest
			//
			
			CreateAccountRequest createAccountRequest = new CreateAccountRequest().withEmail(account.getEmail())
					.withGivenName(account.getGivenName())
					.withMiddleName(account.getMiddleName())
					.withSurname(account.getSurname())
					.withUsername(account.getUsername())
					.withApiKeyId(ConfigurationProvider.getStormpathApiKeyId())
					.withApiKeySecret(ConfigurationProvider.getStormpathApiKeySecret());
			
			//
			// execute the CreateAcountRequest
			//
			
			CreateAccountResponse createAccountResponse = identityProviderClient.account(createAccountRequest);
			
			//
			// throw exception for any issue with the identity provider
			//
			
			if (createAccountResponse.getStatusCode() != 201) {
				throw new IOException(createAccountResponse.getErrorMessage());
			}
			
			//
			//
			//
			
			href = createAccountResponse.getAccount().getHref();
			
		} else {
			
			//
			// build the UpdateAccountRequest
			//
			
			UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest().withApiKeyId(ConfigurationProvider.getStormpathApiKeyId())
					.withApiKeySecret(ConfigurationProvider.getStormpathApiKeySecret())
					.withGivenName(account.getGivenName())
					.withEmail(account.getEmail())
					.withMiddleName(account.getMiddleName())
					.withSurname(account.getSurname())
					.withHref(searchAccountResponse.getItems().get(0).getHref());
			
			//
			// execute the UpdateAccountRequest
			//
			
			UpdateAccountResponse updateAccountResponse = identityProviderClient.account(updateAccountRequest);	
			
			//
			// throw exception for any issue with the identity provider
			//
			
			if (updateAccountResponse.getStatusCode() != 200) {
				throw new IOException(updateAccountResponse.getErrorMessage());
			}
			
			//
			//
			//
			
			href = updateAccountResponse.getAccount().getHref();
			
		}
		
		//
		//
		//
		
		logger.log(new Date() + " " + href);
		
		//
		//
		//
		
		createUserEvent(event.getEventSource(), account.getUsername(), href);
		
		//
		//
		//
		
		event.setTargetId(href);
	}
	
	private void createUserEvent(String eventSource, String username, String href) throws JsonProcessingException {
		
		//
		//
		//
		
		User user = new User();
		user.setUsername(username);
		user.setAccountHref(href);
		
		//
		//
		//
				
		String payload = objectMapper.writeValueAsString(user);
		
		//
		//
		//
		
		Event event = new Event().withEventDate(Date.from(Instant.now()))
				.withEventStatus(Event.EventStatus.NEW)
				.withType(User.class.getName())
				.withEventSource(eventSource)
				.withOrganizationId(ConfigurationProvider.getDefaultOrganizationId())
				.withUserId(ConfigurationProvider.getDefaultUserId())
				.withPayload(payload);
		
		//
		//
		//
		
		DynamoDBMapperProvider.getDynamoDBMapper().save(event);
	}
}