package com.nowellpoint.aws.lambda.sforce;

import java.io.IOException;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.sforce.RefreshTokenRequest;
import com.nowellpoint.aws.model.sforce.RefreshTokenResponse;
import com.nowellpoint.aws.model.sforce.Token;

public class RefreshToken implements RequestHandler<RefreshTokenRequest, RefreshTokenResponse> {
	
	private static final Logger log = Logger.getLogger(RefreshToken.class.getName());

	@Override
	public RefreshTokenResponse handleRequest(RefreshTokenRequest request, Context context) { 
		
		/**
		 * 
		 */
		
		RefreshTokenResponse response = new RefreshTokenResponse();
			
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(Configuration.getSalesforceRefreshUri())
					.header("Content-type", "application/x-www-form-urlencoded")
					.parameter("grant_type", "refresh_token")
					.parameter("client_id", Configuration.getSalesforceClientId())
					.parameter("client_secret", Configuration.getSalesforceClientSecret())
					.parameter("refresh_token", request.getRefreshToken())
					.execute();
			
			log.info("Revoke response status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			/**
			 * 
			 */
				
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (response.getStatusCode() < 400) {		
				response.setToken(httpResponse.getEntity(Token.class));
			} else {
				JsonNode errorResponse = httpResponse.getEntity(JsonNode.class);
				response.setErrorCode(errorResponse.get("error").asText());
				response.setErrorMessage(errorResponse.get("error_description").asText());
			}
			
		} catch (IOException e) {
			log.severe(e.getMessage());
			response.setStatusCode(400);
			response.setErrorCode("invalid_request");
			response.setErrorMessage(e.getMessage());
		}
		
		return response;
	}
}