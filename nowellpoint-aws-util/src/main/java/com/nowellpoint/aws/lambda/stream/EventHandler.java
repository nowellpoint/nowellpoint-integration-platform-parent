package com.nowellpoint.aws.lambda.stream;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.DynamoDBMapperProvider;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.Lead;
import com.nowellpoint.aws.model.idp.Account;
import com.nowellpoint.aws.model.idp.CreateAccountRequest;
import com.nowellpoint.aws.model.idp.CreateAccountResponse;
import com.nowellpoint.aws.model.sforce.CreateLeadRequest;
import com.nowellpoint.aws.model.sforce.CreateLeadResponse;
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
				} else if (Account.class.getName().equals(event.getType())) {
					Account account = objectMapper.readValue(event.getPayload(), Account.class);
					createAccount(account);
					event.setPayload(objectMapper.writeValueAsString(account));
				}
				event.setEventStatus(Event.EventStatus.COMPLETE.toString());
			} catch (IOException e) {
				event.setErrorMessage(e.getMessage());
				event.setEventStatus(Event.EventStatus.ERROR.toString());
			} finally {
				event.setProcessedDate(Date.from(Instant.now()));
				DynamoDBMapperProvider.getDynamoDBMapper().save(event);
			}
		});
		
		return null;
	}
	
	private void processLead(Lead lead) throws IOException {
		
		//
		// setup Salesforce client
		//
		
		final SalesforceClient client = new SalesforceClient();
		
		//
		// build the GetTokenRequest
		//
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(Configuration.getSalesforceUsername())
				.withPassword(Configuration.getSalesforcePassword())
				.withSecurityToken(Configuration.getSalesforceSecurityToken());
		
		//
		// execute the GetTokenRequest
		//
		
		GetTokenResponse getTokenResponse = client.authenticate(tokenRequest);
		
		//
		// throw an exception if unable to log in
		//
		
		if (getTokenResponse.getStatusCode() != 200) {
			throw new IOException(getTokenResponse.getErrorMessage());
		}
		
		log.info("GetTokenResponse status code: " + getTokenResponse.getStatusCode());
		
		//
		// build the CreateLeadRequest
		//
		
		CreateLeadRequest createLeadRequest = new CreateLeadRequest().withAccessToken(getTokenResponse.getToken().getAccessToken())
				.withInstanceUrl(getTokenResponse.getToken().getInstanceUrl())
				.withLead(lead);
		
		//
		// execute the CreateLeadRequest
		//
		
		CreateLeadResponse createLeadResponse = client.createLead(createLeadRequest);
		
		//
		// throw an exception if there is an issue with creating the lead
		//
		
		if (createLeadResponse.getStatusCode() != 201) {
			throw new IOException(createLeadResponse.getErrorMessage());
		}
		
		log.info("CreateLeadResponse status code: " + createLeadResponse.getId());
	}
	
	private void createAccount(Account account) throws IOException {
		
		//
		// setup IdentityProviderClient
		//
		
		final IdentityProviderClient identityProviderClient = new IdentityProviderClient();
		
		//
		// build the CreateAccountRequest
		//
		
		CreateAccountRequest createAccountRequest = new CreateAccountRequest().withAccount(account)
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
		
		log.info(createAccountResponse.getAccount().getHref());
	}
}