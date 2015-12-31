package com.nowellpoint.aws.event;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.DynamoDBMapperProvider;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.data.User;
import com.nowellpoint.aws.model.idp.Account;
import com.nowellpoint.aws.model.idp.CreateAccountRequest;
import com.nowellpoint.aws.model.idp.CreateAccountResponse;
import com.nowellpoint.aws.model.idp.SearchAccountRequest;
import com.nowellpoint.aws.model.idp.SearchAccountResponse;
import com.nowellpoint.aws.model.idp.UpdateAccountRequest;
import com.nowellpoint.aws.model.idp.UpdateAccountResponse;

public class AccountEventHandler implements AbstractEventHandler {
	
	private static final Logger log = Logger.getLogger(AccountEventHandler.class.getName());

	@Override
	public String process(String payload) throws IOException {
		
		log.info("starting AccountEventHandler");
		
		//
		// parse the event payload
		//
		
		Account account = objectMapper.readValue(payload, Account.class);
		
		//
		// setup IdentityProviderClient
		//
		
		final IdentityProviderClient identityProviderClient = new IdentityProviderClient();
		
		//
		// search for exsiting account with username
		//
		
		SearchAccountRequest searchAccountRequest = new SearchAccountRequest().withApiKeyId(Configuration.getStormpathApiKeyId())
				.withApiKeySecret(Configuration.getStormpathApiKeySecret())
				.withUsername(account.getUsername());
		
		SearchAccountResponse searchAccountResponse = identityProviderClient.search(searchAccountRequest);
		
		log.info("found: " + searchAccountResponse.getSize());
		
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
					.withApiKeyId(Configuration.getStormpathApiKeyId())
					.withApiKeySecret(Configuration.getStormpathApiKeySecret());
			
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
			
			UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest().withApiKeyId(Configuration.getStormpathApiKeyId())
					.withApiKeySecret(Configuration.getStormpathApiKeySecret())
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
		
		log.info(href);
		
		//
		//
		//
		
		User user = new User();
		user.setUsername(account.getUsername());
		user.setAccountHref(href);
		
		//
		//
		//
				
		payload = new ObjectMapper().writeValueAsString(user);
		
		//
		//
		//
		
		Event event = new Event().withEventDate(Date.from(Instant.now()))
				.withEventStatus(Event.EventStatus.NEW)
				.withType(User.class.getName())
				.withOrganizationId(Configuration.getDefaultOrganizationId())
				.withUserId(Configuration.getDefaultUserId())
				.withPayload(payload);
		
		DynamoDBMapperProvider.getDynamoDBMapper().save(event);
		
		//
		//
		//
		
		return href;
	}
}