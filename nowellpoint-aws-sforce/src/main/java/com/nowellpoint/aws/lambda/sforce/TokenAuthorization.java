package com.nowellpoint.aws.lambda.sforce;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.sforce.GetAuthorizationRequest;
import com.nowellpoint.aws.model.sforce.GetAuthorizationResponse;
import com.nowellpoint.aws.model.sforce.Token;

public class TokenAuthorization implements RequestHandler<GetAuthorizationRequest, GetAuthorizationResponse> {

	@Override
	public GetAuthorizationResponse handleRequest(GetAuthorizationRequest request, Context context) {
		
		/**
		 * 
		 */
		
		LambdaLogger logger = context.getLogger();
		
		/**
		 * 
		 */
		
		GetAuthorizationResponse response = new GetAuthorizationResponse();
		
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(request.getTokenUri())
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.contentType("application/x-www-form-urlencoded")
					.parameter("grant_type", "authorization_code")
					.parameter("code", request.getCode())
					.parameter("client_id", request.getClientId())
					.parameter("client_secret", request.getClientSecret())
					.parameter("redirect_uri", request.getRedirectUri())
					.execute();
			
			logger.log("Token response status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
				
			/**
			 * 
			 */
			
			response.setStatusCode(httpResponse.getStatusCode());
				
			if (response.getStatusCode() < 400) {		
				response.setToken(httpResponse.getEntity(Token.class));
				logger.log("success: " + response.getToken().getId());
			} else {
				JsonNode errorResponse = httpResponse.getEntity(JsonNode.class);
				response.setErrorCode(errorResponse.get("error").asText());
				response.setErrorMessage(errorResponse.get("error_description").asText());
				logger.log("error: " + errorResponse.toString());
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