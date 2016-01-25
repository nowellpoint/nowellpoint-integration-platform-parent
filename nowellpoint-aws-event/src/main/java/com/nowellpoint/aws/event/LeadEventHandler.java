package com.nowellpoint.aws.event;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.sforce.CreateLeadRequest;
import com.nowellpoint.aws.model.sforce.CreateLeadResponse;
import com.nowellpoint.aws.model.sforce.GetTokenRequest;
import com.nowellpoint.aws.model.sforce.GetTokenResponse;
import com.nowellpoint.aws.model.sforce.Lead;

public class LeadEventHandler implements AbstractEventHandler {
	
	private static LambdaLogger logger;

	@Override
	public void process(Event event, Map<String,String> properties, Context context) throws Exception {
		
		//
		//
		//
		
		logger = context.getLogger();
		
		//
		//
		//
		
		logger.log(this.getClass().getName() + " Starting LeadEventHandler");
		
		//
		//
		//
		
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

		//
		// setup Salesforce client
		//
		
		final SalesforceClient client = new SalesforceClient();

		//
		// build the GetTokenRequest
		//
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withTokenUri(tokenUri)
				.withClientId(clientId)
				.withClientSecret(clientSecret)
				.withUsername(username)
				.withPassword(password)
				.withSecurityToken(securityToken);

		//
		// execute the GetTokenRequest
		//
		
		GetTokenResponse getTokenResponse = client.authenticate(tokenRequest);

		//
		// throw an exception if unable to log in
		//
		
		if (getTokenResponse.getStatusCode() != 200) {
			throw new Exception(getTokenResponse.getErrorMessage());
		}
		
		//
		// build the CreateLeadRequest
		//
		
		CreateLeadRequest createLeadRequest = new CreateLeadRequest()
				.withAccessToken(getTokenResponse.getToken().getAccessToken())
				.withInstanceUrl(getTokenResponse.getToken().getInstanceUrl())
				.withLead(lead);
		
		//
		// execute the CreateLeadRequest
		//
		
		CreateLeadResponse createLeadResponse = client.createLead(createLeadRequest);
		
		//
		// throw an exception if there is an issue with creating the lead
		//
		
		if (createLeadResponse.getStatusCode() != 200 && createLeadResponse.getStatusCode() != 201) {
			logger.log(this.getClass().getName() + " Error: " + createLeadResponse.getErrorCode() + " : " + createLeadResponse.getErrorMessage());
			throw new Exception(createLeadResponse.getErrorMessage());
		}
		
		logger.log(this.getClass().getName() + " Created Lead: " + createLeadResponse.getId());
		
		//
		// return the lead id
		//
		
		event.setTargetId(createLeadResponse.getId());		
	}
}