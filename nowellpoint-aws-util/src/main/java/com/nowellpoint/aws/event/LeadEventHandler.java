package com.nowellpoint.aws.event;

import java.io.IOException;
import java.util.logging.Logger;

import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.Lead;
import com.nowellpoint.aws.model.sforce.CreateLeadRequest;
import com.nowellpoint.aws.model.sforce.CreateLeadResponse;
import com.nowellpoint.aws.model.sforce.GetTokenRequest;
import com.nowellpoint.aws.model.sforce.GetTokenResponse;

public class LeadEventHandler implements AbstractEventHandler {
	
	private static final Logger log = Logger.getLogger(LeadEventHandler.class.getName());

	@Override
	public String process(String payload) throws IOException {
		
		log.info("starting LeadEventHandler");
		
		//
		// parse the event payload
		//
		
		Lead lead = objectMapper.readValue(payload, Lead.class);
		
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
		
		//
		// return the lead id
		//
		
		return createLeadResponse.getId();
		
	}
}