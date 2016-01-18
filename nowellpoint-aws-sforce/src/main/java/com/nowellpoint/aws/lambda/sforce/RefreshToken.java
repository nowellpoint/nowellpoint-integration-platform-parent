package com.nowellpoint.aws.lambda.sforce;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.sforce.RefreshTokenRequest;
import com.nowellpoint.aws.model.sforce.RefreshTokenResponse;
import com.nowellpoint.aws.model.sforce.Token;

public class RefreshToken implements RequestHandler<RefreshTokenRequest, RefreshTokenResponse> {
	
	private static LambdaLogger logger;

	@Override
	public RefreshTokenResponse handleRequest(RefreshTokenRequest request, Context context) { 
		
		//
		//
		//
		
		logger = context.getLogger();
		
		//
		//
		//
		
		RefreshTokenResponse response = new RefreshTokenResponse();
			
		//
		//
		//
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(request.getRefreshTokenUri())
					.header("Content-type", "application/x-www-form-urlencoded")
					.parameter("grant_type", "refresh_token")
					.parameter("client_id", request.getClientId())
					.parameter("client_secret", request.getClientSecret())
					.parameter("refresh_token", request.getRefreshToken())
					.execute();
			
			logger.log("Revoke response status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			//
			//
			//
				
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (response.getStatusCode() < 400) {		
				response.setToken(httpResponse.getEntity(Token.class));
			} else {
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