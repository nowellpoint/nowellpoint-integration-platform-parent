package com.nowellpoint.aws.lambda.idp;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.AuthToken;
import com.nowellpoint.aws.idp.model.VerifyTokenRequest;
import com.nowellpoint.aws.idp.model.VerifyTokenResponse;

public class VerifyToken implements RequestHandler<VerifyTokenRequest, VerifyTokenResponse> {
	
	private static LambdaLogger logger;

	@Override
	public VerifyTokenResponse handleRequest(VerifyTokenRequest request, Context context) { 
		
		//
		//
		//
		
		logger = context.getLogger();
		
		/**
		 * 
		 */
		
		VerifyTokenResponse response = new VerifyTokenResponse();
			
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.get(request.getApiEndpoint())
					.path("applications")
					.basicAuthorization(request.getApiKeyId(), request.getApiKeySecret())
					.path(request.getApplicationId())
					.path("authTokens")
					.path(request.getAccessToken())
					.execute();
				
			logger.log("Status Code: " + response.getStatusCode() + " Target: " + httpResponse.getURL());
							
			/**
			 * 
			 */
			
			response.setStatusCode(httpResponse.getStatusCode());
				
			if (httpResponse.getStatusCode() == 200) {						
				response.setAuthToken(httpResponse.getEntity(AuthToken.class));
			} else {
				JsonNode errorResponse = httpResponse.getEntity(JsonNode.class);
				response.setErrorCode(errorResponse.get("message").asText());
				response.setErrorMessage(errorResponse.get("developerMessage").asText());
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