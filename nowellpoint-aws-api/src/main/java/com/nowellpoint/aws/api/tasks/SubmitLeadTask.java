package com.nowellpoint.aws.api.tasks;

import java.util.concurrent.Callable;

import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.api.model.sforce.Lead;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.Error;
import com.nowellpoint.client.sforce.model.Token;

public class SubmitLeadTask implements Callable<Lead> {
	
	private static final Logger LOGGER = Logger.getLogger(SubmitLeadTask.class);
	
	private Lead lead;
	
	public SubmitLeadTask(Lead lead) {
		this.lead = lead;
	}

	@Override
	public Lead call() throws Exception {

		UsernamePasswordGrantRequest request = OauthRequests.USERNAME_PASSWORD_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setUsername(System.getProperty(Properties.SALESFORCE_USERNAME))
				.setPassword(System.getProperty(Properties.SALESFORCE_PASSWORD))
				.setSecurityToken(System.getProperty(Properties.SALESFORCE_SECURITY_TOKEN))
				.build();
		
		OauthAuthenticationResponse response = Authenticators.USERNAME_PASSWORD_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		Token token = response.getToken();
			
		HttpResponse httpResponse = RestResource.post(token.getInstanceUrl())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.path("services/apexrest/nowellpoint/lead")
				.bearerAuthorization(token.getAccessToken())
				.body(lead)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());	
		
		if (httpResponse.getStatusCode() != 200 && httpResponse.getStatusCode() != 201) {
			Error error = httpResponse.getEntity(Error.class);
			throw new Exception(error.getErrorDescription());
		}
		
		String leadId = httpResponse.getAsString();
		
		lead.setId(leadId);
		
		return lead;
	}
}