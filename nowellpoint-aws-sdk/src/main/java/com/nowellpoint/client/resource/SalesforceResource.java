package com.nowellpoint.client.resource;

import com.nowellpoint.aws.http.MediaType;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.Token;
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
	
	public GetResult<String> getOauthRedirect() {
		HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
    			.path(RESOURCE_CONTEXT)
    			.path("oauth")
    			.execute();
		
		GetResult<String> result = null;
		
		if (httpResponse.getStatusCode() == Status.ACCEPTED) {
			String oauthRedirect = httpResponse.getHeaders().get("Location");
			result = new GetResultImpl<String>(oauthRedirect);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new GetResultImpl<String>(error);
		}
		
		return result;	
	}
	
	/**
	 * 
	 * @param code query param return from Salesforce OAuth request
	 * @return the Salesforce OAuth Token
	 */
	
	public GetResult<OauthToken> getOauthToken(String code) {
		HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.bearerAuthorization(token.getAccessToken())
    			.path(RESOURCE_CONTEXT)
    			.path("oauth")
    			.path("token")
    			.queryParameter("code", code)
    			.execute();
		
		GetResult<OauthToken> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			OauthToken resource = httpResponse.getEntity(OauthToken.class);
			result = new GetResultImpl<OauthToken>(resource);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new GetResultImpl<OauthToken>(error);
		}
		
		return result;
	}
}