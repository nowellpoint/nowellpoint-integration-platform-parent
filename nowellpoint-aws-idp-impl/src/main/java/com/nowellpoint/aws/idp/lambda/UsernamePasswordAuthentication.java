package com.nowellpoint.aws.idp.lambda;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.GetTokenRequest;
import com.nowellpoint.aws.idp.model.GetTokenResponse;
import com.nowellpoint.aws.idp.model.Token;

public class UsernamePasswordAuthentication implements RequestHandler<GetTokenRequest, GetTokenResponse> {
	
	private static LambdaLogger logger;

	@Override
	public GetTokenResponse handleRequest(GetTokenRequest request, Context context) { 

		//
		//
		//
		
		logger = context.getLogger();

		/**
		 * 
		 */
		
		GetTokenResponse response = new GetTokenResponse();
		
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
					.parameter("grant_type", "password")
					.parameter("username", request.getUsername())
					.parameter("password", request.getPassword())
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