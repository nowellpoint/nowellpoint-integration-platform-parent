package com.nowellpoint.client.sforce;

import java.nio.charset.StandardCharsets;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.client.sforce.AuthorizationGrantRequest;
import com.nowellpoint.client.sforce.impl.OauthAuthorizationGrantResponseImpl;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;

public class Authenticators {
	
	public static final AuthorizationGrantResponseFactory AUTHORIZATION_GRANT_AUTHENTICATOR = new AuthorizationGrantResponseFactory();
	
	public static class AuthorizationGrantResponseFactory {
		public OauthAuthorizationGrantResponse authenticate(AuthorizationGrantRequest authorizationGrantRequest) {
			HttpResponse httpResponse;
			
			Token token = null;
			
			httpResponse = RestResource.post(System.getProperty("salesforce.token.uri"))
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.contentType("application/x-www-form-urlencoded")
					.parameter("grant_type", "authorization_code")
					.parameter("code", authorizationGrantRequest.getCode())
					.parameter("client_id", authorizationGrantRequest.getClientId())
					.parameter("client_secret", authorizationGrantRequest.getClientSecret())
					.parameter("redirect_uri", authorizationGrantRequest.getCallbackUri())
					.execute();
			
			if (httpResponse.getStatusCode() >= 400) {
				
			}
			
			token = httpResponse.getEntity(Token.class);
			
			Identity identity = null;
			
			httpResponse = RestResource.get(token.getId())
					.acceptCharset(StandardCharsets.UTF_8)
					.bearerAuthorization(token.getAccessToken())
					.accept(MediaType.APPLICATION_JSON)
					.queryParameter("version", "latest")
					.execute();
	    	
	    	if (httpResponse.getStatusCode() >= 400) {
				
			}
	    	
	    	identity = httpResponse.getEntity(Identity.class);
			
			OauthAuthorizationGrantResponse response = new OauthAuthorizationGrantResponseImpl(token, identity);
			return response;
		}
	}
}