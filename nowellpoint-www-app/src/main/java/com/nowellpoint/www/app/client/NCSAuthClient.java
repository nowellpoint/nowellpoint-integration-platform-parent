package com.nowellpoint.www.app.client;

import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;

public class NCSAuthClient {
	
	private static final Logger LOGGER = Logger.getLogger(NCSAuthClient.class.getName());
	private static final String API_ENDPOINT = System.getenv("NCS_API_ENDPOINT");
	private static final String API_KEY = System.getenv("NCS_API_KEY");
	
	public Token authenticate(String username, String password) throws AuthenticationException {
		
		Token token = null;
		try {
			HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
					.accept(MediaType.APPLICATION_JSON)
					.header("x-api-key", API_KEY)
					.path("oauth")
					.path("token")
					.basicAuthorization(username, password)
					.execute();
			
			int statusCode = httpResponse.getStatusCode();
			
			LOGGER.info("Authenticate Status Code: " + statusCode + " Method: POST : " + httpResponse.getURL());
			
			if (statusCode != 200) {
				throw new AuthenticationException(httpResponse.getEntity());
			}
			
			token = httpResponse.getEntity(Token.class);
			
		} catch (IOException e) {
			throw new IdentityProviderException(e);
		}
	
    	return token;
	}

	public Account getAccount(String accessToken) {
		
		Account account = null;
		try {
			HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
					.accept(MediaType.APPLICATION_JSON)
					.header("x-api-key", API_KEY)
					.bearerAuthorization(accessToken)
					.path("account")
					.execute();
			
			int statusCode = httpResponse.getStatusCode();
	    	
	    	LOGGER.info("Status Code: " + statusCode + " Method: GET : " + httpResponse.getURL());
	    	
	    	if (statusCode != 200) {
	    		throw new IdentityProviderException(httpResponse.getEntity());
	    	}
	    	 	    	
	    	account = new ObjectMapper().readValue(httpResponse.getEntity(), Account.class);
			
		} catch (IOException e) {
			throw new IdentityProviderException(e);
		}
		
		return account;
	}
	
	public void logout(String accessToken) {
		
		try {
			HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
					.accept(MediaType.APPLICATION_JSON)
					.header("x-api-key", API_KEY)
					.bearerAuthorization(accessToken)
	    			.path("oauth")
	    			.path("token")
	    			.execute();
	    	
	    	int statusCode = httpResponse.getStatusCode();
	    	
	    	LOGGER.info("Status Code: " + statusCode + " Method: DELETE : " + httpResponse.getURL());
	    	
	    	if (statusCode != 204) {
	    		throw new IdentityProviderException(httpResponse.getEntity());
	    	}
	    	
		} catch (IOException e) {
			throw new IdentityProviderException(e);
		}	
	}
}