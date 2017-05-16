package com.nowellpoint.client.auth;

import com.nowellpoint.client.auth.impl.OauthAuthenticationResponseImpl;
import com.nowellpoint.client.auth.impl.OauthException;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class Authenticators {
	
	public static final PasswordGrantResponseFactory PASSWORD_GRANT_AUTHENTICATOR = new PasswordGrantResponseFactory();
	public static final ClientCredentialsGrantResponseFactory CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR = new ClientCredentialsGrantResponseFactory();
	public static final RevokeTokenResponseFactory REVOKE_TOKEN_INVALIDATOR = new RevokeTokenResponseFactory();
	
	public static class PasswordGrantResponseFactory {
		public OauthAuthenticationResponse authenticate(PasswordGrantRequest grantRequest) {
			
			HttpResponse httpResponse = RestResource.post(grantRequest.getEnvironment().getEnvironmentUrl())
	    			.accept(MediaType.APPLICATION_JSON)
	    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    			.path("oauth")
	    			.path("token")
	    			.basicAuthorization(grantRequest.getUsername(), grantRequest.getPassword())
	    			.parameter("grant_type", "password")
	    			.execute();
		
	    	int statusCode = httpResponse.getStatusCode();
	    	
	    	if (statusCode == Status.OK) {
	    		
	    		Token token = httpResponse.getEntity(Token.class);
		    	OauthAuthenticationResponse response = new OauthAuthenticationResponseImpl(token);
		    	return response;
		    	
	    	} else if (statusCode == Status.BAD_REQUEST) {
	    		Error error = httpResponse.getEntity(Error.class);
	    		throw new OauthException(error.getCode(), error.getErrorMessage());
	    	} else {
	    		throw new ServiceUnavailableException(httpResponse.getAsString());
	    	}
		}
	}
	
	public static class ClientCredentialsGrantResponseFactory {
		public OauthAuthenticationResponse authenticate(ClientCredentialsGrantRequest grantRequest) {
						
			HttpResponse httpResponse = RestResource.post(grantRequest.getEnvironment().getEnvironmentUrl())
	    			.accept(MediaType.APPLICATION_JSON)
	    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    			.path("oauth")
	    			.path("token")
	    			.basicAuthorization(grantRequest.getApiKeyId(), grantRequest.getApiKeySecret())
	    			.parameter("grant_type", "client_credentials")
	    			.execute();
	    			
	    	int statusCode = httpResponse.getStatusCode();
	    			
	    	if (statusCode == Status.OK) {
	    		
	    		Token token = httpResponse.getEntity(Token.class);
		    	OauthAuthenticationResponse response = new OauthAuthenticationResponseImpl(token);
		    	return response;
		    	
	    	} else if (statusCode == Status.BAD_REQUEST) {
	    		Error error = httpResponse.getEntity(Error.class);
	    		throw new OauthException(error.getCode(), error.getErrorMessage());
	    	} else {
	    		throw new ServiceUnavailableException(httpResponse.getAsString());
	    	}
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
	    		throw new OauthException(error.getCode(), error.getErrorMessage());
	    	}
		}
	}
}