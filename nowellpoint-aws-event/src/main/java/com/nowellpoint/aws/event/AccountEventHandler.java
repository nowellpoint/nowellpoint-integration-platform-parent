package com.nowellpoint.aws.event;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.data.dynamodb.Event;
import com.nowellpoint.aws.data.dynamodb.EventAction;
import com.nowellpoint.aws.data.dynamodb.EventBuilder;
import com.nowellpoint.aws.data.dynamodb.EventStatus;
import com.nowellpoint.aws.data.mongodb.AccountProfile;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.SearchResult;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class AccountEventHandler implements AbstractEventHandler {
	
	private static LambdaLogger logger;

	@Override
	public void process(Event event, Map<String, String> properties, Context context) throws Exception {		
		logger = context.getLogger();
		
		logger.log(this.getClass().getName() + " starting AccountEventHandler");
		
		Account account = objectMapper.readValue(event.getPayload(), Account.class);
		
		String directoryId = properties.get(Properties.STORMPATH_DIRECTORY_ID);
		String apiEndpoint = properties.get(Properties.STORMPATH_API_ENDPOINT);
		String apiKeyId = properties.get(Properties.STORMPATH_API_KEY_ID);
		String apiKeySecret = properties.get(Properties.STORMPATH_API_KEY_SECRET);
		
		String href = null;
		
		if (EventAction.ACCOUNT.name().equals(event.getEventAction())) {
			
			HttpResponse httpResponse = RestResource.get(apiEndpoint)
					.basicAuthorization(apiKeyId, apiKeySecret)
					.accept(MediaType.APPLICATION_JSON)
					.path("directories")
					.path(directoryId)
					.path("accounts")
					.path("?username=".concat(account.getUsername()))
					.execute();
			
			SearchResult searchResult = httpResponse.getEntity(SearchResult.class);
			
			if (searchResult.getSize() == 0) {
				
				httpResponse = RestResource.post(apiEndpoint)
						.contentType(MediaType.APPLICATION_JSON)
						.path("directories")
						.path(directoryId)
						.path("accounts")
						.basicAuthorization(apiKeyId, apiKeySecret)
						.body(account)
						.execute();
				
				logger.log("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
				
				account = httpResponse.getEntity(Account.class);
				
				href = account.getHref();
				
				insertIdentityEvent(event, account.getUsername(), href);
				
			} else {
				
				href = searchResult.getItems().get(0).getHref();
				
				httpResponse = RestResource.post(href)
						.contentType(MediaType.APPLICATION_JSON)
						.basicAuthorization(apiKeyId, apiKeySecret)
						.body(account)
						.execute();
				
				logger.log("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
				
			}
		} 
		
		event.setProcessedDate(Date.from(Instant.now()));
		event.setExecutionTime(System.currentTimeMillis() - event.getStartTime());
		event.setEventStatus(EventStatus.COMPLETE.toString());
		event.setTargetId(href);
	}
	
	private void insertIdentityEvent(Event parentEvent, String username, String href) throws JsonProcessingException {		
		DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
		
		AccountProfile identity = new AccountProfile();
		identity.setUsername(username);
		identity.setHref(href);
		
		Event event = new EventBuilder()
				.withSubject(parentEvent.getSubject())
				.withEventAction(EventAction.IDENTITY)
				.withEventSource(parentEvent.getEventSource())
				.withPropertyStore(parentEvent.getPropertyStore())
				.withPayload(identity)
				.withParentEventId(parentEvent.getId())
				.build();
		
		mapper.save(event);
	}
}