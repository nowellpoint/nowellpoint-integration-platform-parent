package com.nowellpoint.aws.lambda.stream;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.DynamoDBMapperProvider;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.Lead;
import com.nowellpoint.aws.model.config.Properties;
import com.nowellpoint.aws.model.sforce.CreateSObjectRequest;
import com.nowellpoint.aws.model.sforce.CreateSObjectResponse;
import com.nowellpoint.aws.model.sforce.GetTokenRequest;
import com.nowellpoint.aws.model.sforce.GetTokenResponse;

public class EventHandler {
	
	private static final Logger log = Logger.getLogger(EventHandler.class.getName());
			
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public String handleEvent(DynamodbEvent dynamodbEvent, Context context) {
		
		System.setProperty("aws.kms.key.id", Configuration.getAwsKmsKeyId());
		
		LambdaLogger logger = context.getLogger();
		
		dynamodbEvent.getRecords().stream().filter(record -> "INSERT".equals(record.getEventName())).forEach(record -> {
			
			/**
			 * 
			 */
			
			logger.log(new Date() + " Event received...Event Id: "
					.concat(record.getEventID())
					.concat(" Event Name: " + record.getEventName()));	
			
			String id = record.getDynamodb().getKeys().get("Id").getS();
			String organizationId = record.getDynamodb().getKeys().get("OrganizationId").getS();
			
			Event event = DynamoDBMapperProvider.getDynamoDBMapper().load(Event.class, id, organizationId);
			
			try {
				if (Lead.class.getName().equals(event.getType())) {
					processLead(event.getPayload());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		return null;
	}
	
	private void processLead(String json) {
		
		com.nowellpoint.aws.model.config.Configuration configuration = DynamoDBMapperProvider.getDynamoDBMapper().load(com.nowellpoint.aws.model.config.Configuration.class, "4877db51-fccf-4e8e-b012-6ba76d4d76f7");
		Properties properties = null;
		try {
			properties = objectMapper.readValue(configuration.getPayload(), Properties.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SalesforceClient client = new SalesforceClient();
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(properties.getSalesforce().getUsername())
				.withPassword(properties.getSalesforce().getPassword())
				.withSecurityToken(properties.getSalesforce().getSecurityToken());
		
		GetTokenResponse tokenResponse = client.authenticate(tokenRequest);
		
		log.info(tokenResponse.getErrorMessage());
		log.info("status code: " + tokenResponse.getStatusCode());
		
		log.info("lead: " + json);
		
		CreateSObjectRequest createSObjectRequest = new CreateSObjectRequest().withAccessToken(tokenResponse.getToken().getAccessToken())
				.withInstanceUrl(tokenResponse.getToken().getInstanceUrl())
				.withSObject(json)
				.withType("Lead");
		
		CreateSObjectResponse createSObjectResponse = client.createSObject(createSObjectRequest);
		
		log.info("status code: " + createSObjectResponse.getStatusCode());
		log.info(createSObjectResponse.getErrorMessage());
		log.info(createSObjectResponse.getId());
	}
}