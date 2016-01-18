package com.nowellpoint.aws.lambda.sforce;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.sforce.RevokeTokenRequest;
import com.nowellpoint.aws.model.sforce.RevokeTokenResponse;

public class RevokeToken implements RequestHandler<RevokeTokenRequest, RevokeTokenResponse> {
	
	private static LambdaLogger logger;

	@Override
	public RevokeTokenResponse handleRequest(RevokeTokenRequest request, Context context) { 
		
		//
		//
		//
		
		logger = context.getLogger();
		
		/**
		 * 
		 */
		
		RevokeTokenResponse response = new RevokeTokenResponse();
			
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(request.getRevokeTokenUri())
					.header("Content-type", "application/x-www-form-urlencoded")
					.parameter("token", request.getAccessToken())
					.execute();
			
			logger.log("Revoke response status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (response.getStatusCode() >= 400) {		
				JsonNode errorResponse = httpResponse.getEntity(JsonNode.class);
				response.setErrorCode(errorResponse.get("error").asText());
				response.setErrorMessage(errorResponse.get("error_description").asText());
			}
			
		} catch (IOException e) {
			logger.log(e.getMessage());
			response.setStatusCode(400);
			response.setErrorCode("invalid_request");
			response.setErrorMessage(e.getMessage());
		}
		
		return response;
	}
}