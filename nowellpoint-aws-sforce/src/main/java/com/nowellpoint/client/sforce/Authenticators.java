package com.nowellpoint.client.sforce;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.client.sforce.AuthorizationGrantRequest;
import com.nowellpoint.client.sforce.model.Token;

public class Authenticators {
	
	public static final AuthorizationGrantResponseFactory AUTHORIZATION_GRANT_AUTHENTICATOR = new AuthorizationGrantResponseFactory();
	
	public class OauthAuthorizationGrantResponseImpl implements OauthAuthorizationGrantResponse{
		
		private Token token;
		
		public OauthAuthorizationGrantResponseImpl(Token token) {
			this.token = token;
		}
		
		public Token getToken() {
			return token;
		}
	}
	
	public static class AuthorizationGrantResponseFactory {
		public OauthAuthorizationGrantResponse authenticate(AuthorizationGrantRequest authorizationGrantRequest) {
			Token token = null;
			
			try {
				HttpResponse httpResponse = RestResource.post(System.getProperty("salesforce.token.uri"))
						.acceptCharset(StandardCharsets.UTF_8)
						.accept(MediaType.APPLICATION_JSON)
						.contentType("application/x-www-form-urlencoded")
						.parameter("grant_type", "authorization_code")
						.parameter("code", authorizationGrantRequest.getCode())
						.parameter("client_id", System.getProperty("salesforce.client.id"))
						.parameter("client_secret", System.getProperty("salesforce.client.secret"))
						.parameter("redirect_uri", System.getProperty("salesforce.redirect.uri"))
						.execute();
				
				if (httpResponse.getStatusCode() >= 400) {
					
				}
				
				token = httpResponse.getEntity(Token.class);
				
			} catch (IOException e) {
				
			}		
			
			Authenticators authenticators = new Authenticators();
			OauthAuthorizationGrantResponse response = authenticators.new OauthAuthorizationGrantResponseImpl(token);
			return response;
		}
	}
}