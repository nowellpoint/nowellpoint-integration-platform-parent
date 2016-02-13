package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.idp.client.IdentityProviderClient;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventBuilder;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.data.Identity;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.CreateAccountRequest;
import com.nowellpoint.aws.idp.model.CreateAccountResponse;
import com.nowellpoint.aws.idp.model.SearchAccountRequest;
import com.nowellpoint.aws.idp.model.SearchAccountResponse;
import com.nowellpoint.aws.idp.model.UpdateAccountRequest;
import com.nowellpoint.aws.idp.model.UpdateAccountResponse;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class AccountEventHandler implements AbstractEventHandler {
	
	private static LambdaLogger logger;

	@Override
	public void process(Event event, Map<String, String> properties, Context context) throws Exception {
		
		//
		//
		//
		
		logger = context.getLogger();
		
		//
		//
		//
		
		logger.log(this.getClass().getName() + " starting AccountEventHandler");
		
		//
		// 
		//
		
		Account account = objectMapper.readValue(event.getPayload(), Account.class);
		
		//
		//
		//
		
		String directoryId = properties.get(Properties.STORMPATH_DIRECTORY_ID);
		String apiEndpoint = properties.get(Properties.STORMPATH_API_ENDPOINT);
		String apiKeyId = properties.get(Properties.STORMPATH_API_KEY_ID);
		String apiKeySecret = properties.get(Properties.STORMPATH_API_KEY_SECRET);
		
		//
		// setup IdentityProviderClient
		//
		
		final IdentityProviderClient identityProviderClient = new IdentityProviderClient();
		
		//
		//
		//
		
		String href = null;
		
		if (EventAction.CREATE.name().equals(event.getEventAction())) {
			
			//
			// search for existing account with username
			//
			
			SearchAccountRequest searchAccountRequest = new SearchAccountRequest()
					.withDirectoryId(directoryId)
					.withApiEndpoint(apiEndpoint)
					.withApiKeyId(apiKeyId)
					.withApiKeySecret(apiKeySecret)
					.withUsername(account.getUsername());
			
			SearchAccountResponse searchAccountResponse = identityProviderClient.account(searchAccountRequest);
			
			logger.log(this.getClass().getName() + " account found: " + (searchAccountResponse.getSize() > 0));
			
			if (searchAccountResponse.getSize() == 0) {
				
				//
				// build the CreateAccountRequest
				//
				
				CreateAccountRequest createAccountRequest = new CreateAccountRequest()
						.withDirectoryId(directoryId)
						.withApiEndpoint(apiEndpoint)
						.withApiKeyId(apiKeyId)
						.withApiKeySecret(apiKeySecret)
						.withEmail(account.getEmail())
						.withGivenName(account.getGivenName())
						.withMiddleName(account.getMiddleName())
						.withSurname(account.getSurname())
						.withUsername(account.getUsername())
						.withPassword(account.getPassword());
				
				//
				// execute the CreateAcountRequest
				//
				
				CreateAccountResponse createAccountResponse = identityProviderClient.account(createAccountRequest);
				
				logger.log(this.getClass().getName() + " status: " + createAccountResponse.getStatusCode());
				logger.log(createAccountResponse.getErrorMessage());
				
				//
				// throw exception for any issue with the identity provider
				//
				
				if (createAccountResponse.getStatusCode() != 201) {
					throw new Exception(createAccountResponse.getErrorMessage());
				}
				
				//
				//
				//
				
				href = createAccountResponse.getAccount().getHref();
				
				//
				//
				//
				
				createUserEvent(event, account.getUsername(), href);
				
			} else {
				
				//
				// build the UpdateAccountRequest
				//
				
				UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest()
						.withApiKeyId(apiKeyId)
						.withApiKeySecret(apiKeySecret)
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
		} else if (EventAction.UPDATE.name().equals(event.getEventAction())) {
			
			//
			// build the UpdateAccountRequest
			//
			
			UpdateAccountRequest updateAccountRequest = new UpdateAccountRequest()
					.withApiKeyId(apiKeyId)
					.withApiKeySecret(apiKeySecret)
					.withGivenName(account.getGivenName())
					.withEmail(account.getEmail())
					.withMiddleName(account.getMiddleName())
					.withSurname(account.getSurname())
					.withHref(account.getHref());
			
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
			
		} else {
			throw new Exception( String.format("Invalid action for %s object: %s", event.getType(), event.getEventAction() ) );
		}

		//
		//
		//
		
		logger.log(this.getClass().getName() + " " + href);
		
		//
		//
		//
		
		event.setTargetId(href);
	}
	
	private void createUserEvent(Event parentEvent, String username, String href) throws JsonProcessingException {
		
		//
		//
		//
		
		DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
		
		//
		//
		//
		
		Identity identity = new Identity();
		identity.setUsername(username);
		identity.setHref(href);
		
		//
		//
		//
		
		Event event = new EventBuilder()
				.withSubject(parentEvent.getSubject())
				.withEventAction(EventAction.UPDATE)
				.withEventSource(parentEvent.getEventSource())
				.withPropertyStore(parentEvent.getPropertyStore())
				.withPayload(identity)
				.withType(Identity.class)
				.withParentEventId(parentEvent.getId())
				.build();
		
		//
		//
		//
		
		mapper.save(event);
	}
}