package com.nowellpoint.aws.lambda.stream;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.DynamoDBMapperProvider;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.Lead;
import com.nowellpoint.aws.model.admin.Options;
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
					Lead lead = objectMapper.readValue(event.getPayload(), Lead.class);
					processLead(lead);
					event.setPayload(objectMapper.writeValueAsString(lead));
				}
				event.setEventStatus(Event.EventStatus.COMPLETE.toString());
			} catch (IOException e) {
				event.setErrorMessage(e.getMessage());
				event.setEventStatus(Event.EventStatus.ERROR.toString());
			} finally {
				DynamoDBMapperProvider.getDynamoDBMapper().save(event);
			}
		});
		
		return null;
	}
	
	private void processLead(Lead lead) throws IOException {
		
		com.nowellpoint.aws.model.admin.Configuration configuration = DynamoDBMapperProvider.getDynamoDBMapper().load(com.nowellpoint.aws.model.admin.Configuration.class, "4877db51-fccf-4e8e-b012-6ba76d4d76f7");
	
		Options options = objectMapper.readValue(configuration.getPayload(), Options.class);
		
		SalesforceClient client = new SalesforceClient();
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(options.getSalesforce().getUsername())
				.withPassword(options.getSalesforce().getPassword())
				.withSecurityToken(options.getSalesforce().getSecurityToken());
		
		GetTokenResponse tokenResponse = client.authenticate(tokenRequest);
		
		log.info("tokenResponse status code: " + tokenResponse.getStatusCode());
		
		log.info("lead: " + objectMapper.writeValueAsString(lead));
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(tokenResponse.getToken().getInstanceUrl())
					.path("services/apexrest/nowellpoint/lead")
					.header("Content-type", MediaType.APPLICATION_JSON)
					.bearerAuthorization(tokenResponse.getToken().getAccessToken())
					.body(objectMapper.writeValueAsString(lead))
					.execute();
		} catch (IOException e) {
			log.severe(e.getMessage());
		}
		
		log.info("Create Lead status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
		
		log.info("return value" + httpResponse.getEntity());
//		CreateSObjectRequest createSObjectRequest = new CreateSObjectRequest().withAccessToken(tokenResponse.getToken().getAccessToken())
//				.withInstanceUrl(tokenResponse.getToken().getInstanceUrl())
//				.withSObject(objectMapper.writeValueAsString(lead))
//				.withType("Lead");
//		
//		CreateSObjectResponse createSObjectResponse = client.createSObject(createSObjectRequest);
//		
//		log.info("status code: " + createSObjectResponse.getStatusCode());
//		if (createSObjectResponse.getStatusCode() != 201) {
//			throw new IOException(createSObjectResponse.getErrorMessage());
//		}
//		log.info(createSObjectResponse.getErrorMessage());
//		log.info(createSObjectResponse.getId());
		
		//lead.setId(createSObjectResponse.getId());
	}
}