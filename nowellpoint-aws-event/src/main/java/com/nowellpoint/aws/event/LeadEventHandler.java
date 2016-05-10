package com.nowellpoint.aws.event;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.data.dynamodb.Event;
import com.nowellpoint.aws.data.dynamodb.EventStatus;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.sforce.Lead;
import com.nowellpoint.aws.model.sforce.Token;

public class LeadEventHandler implements AbstractEventHandler {
	
	private static LambdaLogger logger;

	@Override
	public void process(Event event, Map<String,String> properties, Context context) throws Exception {
		
		logger = context.getLogger();

		logger.log(this.getClass().getName() + " Starting LeadEventHandler");
		
		String tokenUri = properties.get(Properties.SALESFORCE_TOKEN_URI);
		String clientId = properties.get(Properties.SALESFORCE_CLIENT_ID);
		String clientSecret = properties.get(Properties.SALESFORCE_CLIENT_SECRET);
		String username = properties.get(Properties.SALESFORCE_USERNAME);
		String password = properties.get(Properties.SALESFORCE_PASSWORD);
		String securityToken = properties.get(Properties.SALESFORCE_SECURITY_TOKEN);
		
		//
		// parse the event payload
		//
		
		Lead lead = objectMapper.readValue(event.getPayload(), Lead.class);
		
		HttpResponse httpResponse = null;
		
		httpResponse = RestResource.post(tokenUri)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.acceptCharset(StandardCharsets.UTF_8)
				.parameter("grant_type", "password")
				.parameter("client_id", clientId)
				.parameter("client_secret", clientSecret)
				.parameter("username", username)
				.parameter("password", password.concat(securityToken != null ? securityToken : ""))
				.execute();
			
		logger.log("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());	
		
		Token token = null;
		if (httpResponse.getStatusCode() < 400) {		
			token = httpResponse.getEntity(Token.class);
		} else {
			JsonNode errorResponse = httpResponse.getEntity(JsonNode.class);
			logger.log("Error: " + errorResponse.get("error").asText() + " Error Description: " + errorResponse.get("error_description").asText());
			throw new Exception(errorResponse.get("error_description").asText());
		}

		//
		// setup Salesforce client
		//
		
//		final SalesforceClient client = new SalesforceClient();

		//
		// build the GetTokenRequest
		//
		
//		GetTokenRequest tokenRequest = new GetTokenRequest().withTokenUri(tokenUri)
//				.withClientId(clientId)
//				.withClientSecret(clientSecret)
//				.withUsername(username)
//				.withPassword(password)
//				.withSecurityToken(securityToken);

		//
		// execute the GetTokenRequest
		//
		
//		GetTokenResponse getTokenResponse = client.authenticate(tokenRequest);

		//
		// throw an exception if unable to log in
		//
		
//		if (getTokenResponse.getStatusCode() != 200) {
//			throw new Exception(getTokenResponse.getErrorMessage());
//		}
		
		httpResponse = RestResource.post(token.getInstanceUrl())
				.path("services/apexrest/nowellpoint/lead")
				.header("Content-type", MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.body(lead)
				.execute();
		
		if (httpResponse.getStatusCode() != 200 && httpResponse.getStatusCode() != 201) {
			JsonNode errorResponse = httpResponse.getEntity(JsonNode.class);
			logger.log("Error: " + errorResponse.get("error").asText() + " Error Description: " + errorResponse.get("error_description").asText());
			throw new Exception(errorResponse.get("error_description").asText());
		}
		
		
		//
		// build the CreateLeadRequest
		//
		
//		CreateLeadRequest createLeadRequest = new CreateLeadRequest()
//				.withAccessToken(getTokenResponse.getToken().getAccessToken())
//				.withInstanceUrl(getTokenResponse.getToken().getInstanceUrl())
//				.withLead(lead);
		
		//
		// execute the CreateLeadRequest
		//
		
//		CreateLeadResponse createLeadResponse = client.createLead(createLeadRequest);
		
		//
		// throw an exception if there is an issue with creating the lead
		//
		
//		if (createLeadResponse.getStatusCode() != 200 && createLeadResponse.getStatusCode() != 201) {
//			logger.log(this.getClass().getName() + " Error: " + createLeadResponse.getErrorCode() + " : " + createLeadResponse.getErrorMessage());
//			throw new Exception(createLeadResponse.getErrorMessage());
//		}
		
		String leadId = httpResponse.getAsString();	
			
		logger.log(this.getClass().getName() + " Created Lead: " + leadId);
		
		//
		// return the lead id
		//
		
		event.setProcessedDate(Date.from(Instant.now()));
		event.setExecutionTime(System.currentTimeMillis() - event.getStartTime());
		event.setEventStatus(EventStatus.COMPLETE.toString());
		event.setTargetId(leadId);		
	}
}