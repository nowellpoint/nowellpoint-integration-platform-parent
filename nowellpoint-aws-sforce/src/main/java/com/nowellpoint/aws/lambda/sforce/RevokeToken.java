package com.nowellpoint.aws.lambda.sforce;

import java.io.IOException;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.sforce.RevokeTokenRequest;
import com.nowellpoint.aws.model.sforce.RevokeTokenResponse;
import com.nowellpoint.aws.util.Configuration;

public class RevokeToken implements RequestHandler<RevokeTokenRequest, RevokeTokenResponse> {
	
	private static final Logger log = Logger.getLogger(RevokeToken.class.getName());

	@Override
	public RevokeTokenResponse handleRequest(RevokeTokenRequest request, Context context) { 
		
		/**
		 * 
		 */
		
		RevokeTokenResponse response = new RevokeTokenResponse();
			
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(Configuration.getSalesforceRevokeUri())
					.header("Content-type", "application/x-www-form-urlencoded")
					.parameter("token", request.getAccessToken())
					.execute();
			
			log.info("Revoke response status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (response.getStatusCode() >= 400) {		
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