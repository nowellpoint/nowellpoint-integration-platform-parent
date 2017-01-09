package com.nowellpoint.client.resource;

import com.nowellpoint.aws.http.MediaType;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.client.model.sforce.OauthToken;

public class SalesforceResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "salesforce";
	
	public SalesforceResource(Environment environment, Token token) {
		super(environment, token);
	}
	
	/**
	 * 
	 * @return Salesforce OAuth redirect URL
	 */
	
	public String getOauthRedirect() {
		HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
    			.path(RESOURCE_CONTEXT)
    			.path("oauth")
    			.execute();
		
		String oauthRedirect = null;
		
		if (httpResponse.getStatusCode() == Status.ACCEPTED) {
			oauthRedirect = httpResponse.getHeaders().get("Location");
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
		
		return oauthRedirect;	
	}
	
	/**
	 * 
	 * @param code query param return from Salesforce OAuth request
	 * @return the Salesforce OAuth Token
	 */
	
	public OauthToken getOauthToken(String code) {
		HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.bearerAuthorization(token.getAccessToken())
    			.path(RESOURCE_CONTEXT)
    			.path("oauth")
    			.path("token")
    			.queryParameter("code", code)
    			.execute();
		
		OauthToken oauthToken = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			oauthToken = httpResponse.getEntity(OauthToken.class);
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
		
		return oauthToken;
	}
}