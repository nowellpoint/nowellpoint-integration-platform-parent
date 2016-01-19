package com.nowellpoint.aws.idp.lambda;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.RefreshTokenRequest;
import com.nowellpoint.aws.idp.model.RefreshTokenResponse;
import com.nowellpoint.aws.idp.model.Token;

public class RefreshToken implements RequestHandler<RefreshTokenRequest, RefreshTokenResponse> {
	
	private static LambdaLogger logger;

	@Override
	public RefreshTokenResponse handleRequest(RefreshTokenRequest request, Context context) { 
		
		//
		//
		//
		
		logger = context.getLogger();
		
		/**
		 * 
		 */
		
		RefreshTokenResponse response = new RefreshTokenResponse();
			
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(request.getApiEndpoint())
					.path("applications")
					.path(request.getApplicationId())
					.path("oauth/token")
					.basicAuthorization(request.getApiKeyId(), request.getApiKeySecret())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.parameter("grant_type", "refresh_token")
					.parameter("refresh_token", request.getRefreshToken())
					.execute();
			
			logger.log("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			/**
			 * 
			 */
							
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == 200) {						
				response.setToken(httpResponse.getEntity(Token.class));
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