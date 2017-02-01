package com.nowellpoint.client.auth;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.auth.impl.OauthAuthenticationResponseImpl;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.util.Assert;

public class Authenticators {
	
	public static final PasswordGrantResponseFactory PASSWORD_GRANT_AUTHENTICATOR = new PasswordGrantResponseFactory();
	public static final ClientCredentialsGrantResponseFactory CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR = new ClientCredentialsGrantResponseFactory();
	public static final RevokeTokenResponseFactory REVOKE_TOKEN_INVALIDATOR = new RevokeTokenResponseFactory();
	
	public static class PasswordGrantResponseFactory {
		public OauthAuthenticationResponse authenticate(PasswordGrantRequest grantRequest) {
			
			Assert.assertNotNull(grantRequest.getUsername(), "missing username");
			Assert.assertNotNull(grantRequest.getPassword(), "missing password");
			Assert.assertNotNull(grantRequest.getEnvironment(), "environment has not been set");
			
			HttpResponse httpResponse = RestResource.post(grantRequest.getEnvironment().getEnvironmentUrl())
	    			.accept(MediaType.APPLICATION_JSON)
	    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    			.path("oauth")
	    			.path("token")
	    			.basicAuthorization(grantRequest.getUsername(), grantRequest.getPassword())
	    			.parameter("grant_type", "password")
	    			.execute();
		
	    	int statusCode = httpResponse.getStatusCode();

	    	if (statusCode != Status.OK) {
	    		Error error = httpResponse.getEntity(Error.class);
	    		throw new OauthException(error.getCode(), error.getMessage());
	    	}
	
	    	Token token = httpResponse.getEntity(Token.class);
	    	
	    	OauthAuthenticationResponse response = new OauthAuthenticationResponseImpl(token);
	    	
	    	return response;
		}
	}
	
	public static class ClientCredentialsGrantResponseFactory {
		public OauthAuthenticationResponse authenticate(ClientCredentialsGrantRequest grantRequest) {
			
			Assert.assertNotNull(grantRequest.getEnvironment(), "environment has not been set");
			Assert.assertNotNull(grantRequest.getApiKeyId(), "missing api key id");
			Assert.assertNotNull(grantRequest.getApiKeySecret(), "missing api key secret");
			Assert.assertNotNull(grantRequest.getEnvironment(), "environment has not been set");
			
			HttpResponse httpResponse = RestResource.post(grantRequest.getEnvironment().getEnvironmentUrl())
	    			.accept(MediaType.APPLICATION_JSON)
	    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    			.path("oauth")
	    			.path("token")
	    			.basicAuthorization(grantRequest.getApiKeyId(), grantRequest.getApiKeySecret())
	    			.parameter("grant_type", "client_credentials")
	    			.execute();
	    			
	    	int statusCode = httpResponse.getStatusCode();
	    			
	    	if (statusCode != Status.OK) {
	    		Error error = httpResponse.getEntity(Error.class);
	    		throw new OauthException(error.getCode(), error.getMessage());
	    	}
	    			
	    	Token token = httpResponse.getEntity(Token.class);
	    	
	    	OauthAuthenticationResponse response = new OauthAuthenticationResponseImpl(token);
	    	
	    	return response;
		}
	}
	
	public static class RevokeTokenResponseFactory {
		public void revoke(RevokeTokenRequest revokeTokenRequest) {
			HttpResponse httpResponse = RestResource.delete(revokeTokenRequest.getToken().getEnvironmentUrl())
					.bearerAuthorization(revokeTokenRequest.getToken().getAccessToken())
	    			.path("oauth")
	    			.path("token")
	    			.execute();
	    	
	    	int statusCode = httpResponse.getStatusCode();
	    	
	    	if (statusCode != 204) {
	    		Error error = httpResponse.getEntity(Error.class);
	    		throw new OauthException(error.getCode(), error.getMessage());
	    	}
		}
	}
}