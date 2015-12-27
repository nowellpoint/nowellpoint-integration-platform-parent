package com.nowellpoint.aws.lambda.stream;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.DynamoDBMapperProvider;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.Lead;
import com.nowellpoint.aws.model.admin.Options;
import com.nowellpoint.aws.model.idp.Account;
import com.nowellpoint.aws.model.idp.CreateAccountRequest;
import com.nowellpoint.aws.model.idp.CreateAccountResponse;
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
		
		// [{"errorCode":"APEX_ERROR","message":"System.DmlException: Insert failed. First exception on row 0; first error: REQUIRED_FIELD_MISSING, Required fields are missing: [DurationInMinutes]: [DurationInMinutes]\n\nClass.LeadResource.doPost: line 49, column 1"}]
		log.info("Create Lead status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
		log.info("return value" + httpResponse.getEntity());
	}
	
	private void createAccount(Account account) throws IOException {
		
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
		
		if (createAccountResponse.getStatusCode() != 201) {
			throw new IOException(createAccountResponse.getErrorMessage());
		}
		
		log.info(createAccountResponse.getAccount().getHref());
	}
}