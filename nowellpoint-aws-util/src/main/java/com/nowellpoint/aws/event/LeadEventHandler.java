package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.Date;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.Lead;
import com.nowellpoint.aws.model.sforce.CreateLeadRequest;
import com.nowellpoint.aws.model.sforce.CreateLeadResponse;
import com.nowellpoint.aws.model.sforce.GetTokenRequest;
import com.nowellpoint.aws.model.sforce.GetTokenResponse;
import com.nowellpoint.aws.provider.ConfigurationProvider;

public class LeadEventHandler implements AbstractEventHandler {

	@Override
	public void process(Event event, Context context) throws IOException {
		
		LambdaLogger logger = context.getLogger();
		
		logger.log(new Date() + " starting LeadEventHandler");
		
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
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(ConfigurationProvider.getSalesforceUsername())
				.withPassword(ConfigurationProvider.getSalesforcePassword())
				.withSecurityToken(ConfigurationProvider.getSalesforceSecurityToken());
		
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
		
		logger.log(new Date() + " GetTokenResponse status code: " + getTokenResponse.getStatusCode());
		
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
		
		logger.log(new Date() + " CreateLeadResponse status code: " + createLeadResponse.getId());
		
		//
		// return the lead id
		//
		
		event.setTargetId(createLeadResponse.getId());
		
	}
}