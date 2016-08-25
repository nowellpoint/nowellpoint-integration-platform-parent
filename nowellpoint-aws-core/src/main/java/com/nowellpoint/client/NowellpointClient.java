package com.nowellpoint.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.BasicCredentials;
import com.nowellpoint.client.auth.Credentials;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.auth.impl.OauthException;

public class NowellpointClient {
	
	private static final String API_ENDPOINT = System.getenv("NCS_API_ENDPOINT");
	private static Token token;
	
	public NowellpointClient(Credentials credentials) {
		if (credentials instanceof BasicCredentials) {
			PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
					.setUsername(((BasicCredentials) credentials).getUsername())
					.setPassword(((BasicCredentials) credentials).getPassword())
					.build();
		
			OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
						.authenticate(passwordGrantRequest);
			
			token = oauthAuthenticationResponse.getToken();
		}
	}
	
	public void logout() {
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
    			.path("oauth")
    			.path("token")
    			.execute();
    	
    	int statusCode = httpResponse.getStatusCode();
    	
    	if (statusCode != 204) {
    		ObjectNode error = httpResponse.getEntity(ObjectNode.class);
    		throw new OauthException(error.get("code").asInt(), error.get("message").asText());
    	}
	}
}