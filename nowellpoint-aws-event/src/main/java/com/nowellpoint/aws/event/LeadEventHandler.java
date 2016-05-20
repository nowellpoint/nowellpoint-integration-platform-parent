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
import com.nowellpoint.aws.event.model.Lead;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.admin.Property;
import com.nowellpoint.client.sforce.model.Token;

public class LeadEventHandler implements AbstractEventHandler {
	
	private static LambdaLogger logger;

	@Override
	public void process(Event event, Map<String,Property> properties, Context context) throws Exception {
		
		logger = context.getLogger();

		logger.log(this.getClass().getName() + " Starting LeadEventHandler");
		
		String tokenUri = properties.get(Properties.SALESFORCE_TOKEN_URI).getValue();
		String clientId = properties.get(Properties.SALESFORCE_CLIENT_ID).getValue();
		String clientSecret = properties.get(Properties.SALESFORCE_CLIENT_SECRET).getValue();
		String username = properties.get(Properties.SALESFORCE_USERNAME).getValue();
		String password = properties.get(Properties.SALESFORCE_PASSWORD).getValue();
		String securityToken = properties.get(Properties.SALESFORCE_SECURITY_TOKEN).getValue();
		
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
		
		httpResponse = RestResource.post(token.getInstanceUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.path("services/apexrest/nowellpoint/lead")
				.bearerAuthorization(token.getAccessToken())
				.body(lead)
				.execute();
		
		logger.log("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());	
		
		if (httpResponse.getStatusCode() != 200 && httpResponse.getStatusCode() != 201) {
			JsonNode errorResponse = httpResponse.getEntity(JsonNode.class);
			logger.log("Error: " + errorResponse.get("error").asText() + " Error Description: " + errorResponse.get("error_description").asText());
			throw new Exception(httpResponse.getAsString());
		}
		
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