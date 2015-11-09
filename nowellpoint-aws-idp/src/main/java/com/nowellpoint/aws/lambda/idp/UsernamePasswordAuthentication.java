package com.nowellpoint.aws.lambda.idp;

import java.io.IOException;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.lambda.idp.model.GetTokenRequest;
import com.nowellpoint.aws.lambda.idp.model.GetTokenResponse;
import com.nowellpoint.aws.lambda.idp.model.IdpException;
import com.nowellpoint.aws.lambda.idp.model.Token;
import com.nowellpoint.aws.util.Configuration;

public class UsernamePasswordAuthentication implements RequestHandler<GetTokenRequest, GetTokenResponse> {
	
	private static final Logger log = Logger.getLogger(UsernamePasswordAuthentication.class.getName());
	private static final String endpoint = "https://api.stormpath.com/v1/applications";

	@Override
	public GetTokenResponse handleRequest(GetTokenRequest tokenRequest, Context context) { 
			
		/**
		 * 
		 */
		
		GetTokenResponse tokenResponse = new GetTokenResponse();
		
		/**
		 * 
		 */
		
		HttpResponse response = null;
		try {
			response = RestResource.post(endpoint)
					.path(Configuration.getStormpathApplicationId())
					.path("oauth/token")
					.basicAuthorization(Configuration.getStormpathApiKeyId(), Configuration.getStormpathApiKeySecret())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.parameter("grant_type", "password")
					.parameter("username", tokenRequest.getUsername())
					.parameter("password", tokenRequest.getPassword())
					.execute();
			
			log.info("Status Code: " + response.getStatusCode() + " Target: " + response.getURL());			
			
			/**
			 * 
			 */
			
			tokenResponse.setStatusCode(response.getStatusCode());
				
			if (response.getStatusCode() == 200) {						
				tokenResponse.setToken(response.getEntity(Token.class));
			} else {
				IdpException exception = response.getEntity(IdpException.class);
				tokenResponse.setErrorCode(exception.getError());
				tokenResponse.setErrorMessage(exception.getMessage());
			}
			
		} catch (IOException e) {
			log.severe(e.getMessage());
			tokenResponse.setStatusCode(400);
			tokenResponse.setErrorCode("invalid_request");
			tokenResponse.setErrorMessage(e.getMessage());
		}
		
		return tokenResponse;
	}
}