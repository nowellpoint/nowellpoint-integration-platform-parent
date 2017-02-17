package com.nowellpoint.client.resource;

import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.client.model.sforce.OauthToken;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class SalesforceResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "salesforce";
	
	public SalesforceResource(Token token) {
		super(token);
	}
	
	/**
	 * 
	 * @return Salesforce OAuth redirect URL
	 */
	
	public String getOauthRedirect() {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
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
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
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