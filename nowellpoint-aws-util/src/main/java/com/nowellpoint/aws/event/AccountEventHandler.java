package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventBuilder;
import com.nowellpoint.aws.model.admin.Configuration;
import com.nowellpoint.aws.model.data.Identity;
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
		
		//
		//
		//
		
		LambdaLogger logger = context.getLogger();
		
		//
		//
		//
		
		logger.log(new Date() + " starting AccountEventHandler");
		
		//
		//
		//
		
		Configuration configuration = ConfigurationProvider.getConfiguration(event.getConfigurationId());
		
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
		
		SearchAccountRequest searchAccountRequest = new SearchAccountRequest().withApiKeyId(configuration.getStormpathApiKeyId())
				.withApiKeySecret(configuration.getStormpathApiKeySecret())
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
					.withApiKeyId(configuration.getStormpathApiKeyId())
					.withApiKeySecret(configuration.getStormpathApiKeySecret());
			
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
			
			UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest().withApiKeyId(configuration.getStormpathApiKeyId())
					.withApiKeySecret(configuration.getStormpathApiKeySecret())
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
		
		createUserEvent(event, configuration, account.getUsername(), href);
		
		//
		//
		//
		
		event.setTargetId(href);
	}
	
	private void createUserEvent(Event parentEvent, Configuration configuration, String username, String href) throws JsonProcessingException {
		
		//
		//
		//
		
		DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
		
		//
		//
		//
		
		Identity identity = new Identity();
		identity.setUsername(username);
		identity.setAccountHref(href);
		
		//
		//
		//
		
		Event event = new EventBuilder().withAccountId(configuration.getDefaultAccountId())
				.withConfigurationId(configuration.getId())
				.withEventAction(EventAction.UPDATE)
				.withEventSource(parentEvent.getEventSource())
				.withOrganizationId(configuration.getDefaultOrganizationId())
				.withPayload(identity)
				.withType(Identity.class)
				.build();
		
		//
		//
		//
		
		mapper.save(event);
	}
}