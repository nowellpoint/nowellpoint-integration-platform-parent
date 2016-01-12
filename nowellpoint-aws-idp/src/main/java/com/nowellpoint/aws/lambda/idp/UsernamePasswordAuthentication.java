package com.nowellpoint.aws.lambda.idp;

import java.io.IOException;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.idp.GetTokenRequest;
import com.nowellpoint.aws.model.idp.GetTokenResponse;
import com.nowellpoint.aws.model.idp.Token;

public class UsernamePasswordAuthentication implements RequestHandler<GetTokenRequest, GetTokenResponse> {
	
	private static final Logger log = Logger.getLogger(UsernamePasswordAuthentication.class.getName());

	@Override
	public GetTokenResponse handleRequest(GetTokenRequest request, Context context) { 
		
		LambdaLogger logger = context.getLogger();
		logger.log(request.getApiEndpoint());
		logger.log(request.getApplicationId());
		logger.log(request.getApiKeyId());
		logger.log(request.getApiKeySecret());
		logger.log(request.getPassword());
		logger.log(request.getUsername());
			
		/**
		 * 
		 */
		
		GetTokenResponse response = new GetTokenResponse();
		
		/**
		 * 
		 */
		
		try {
			validate(request);
		} catch (IllegalArgumentException e) {
			response.setStatusCode(400);
			response.setErrorCode("invalid_request");
			response.setErrorMessage(e.getMessage());
			return response;
		}
		
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
			
			log.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());			
			
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
			log.severe(e.getMessage());
			response.setStatusCode(400);
			response.setErrorCode("invalid_request");
			response.setErrorMessage(e.getMessage());
		}
		
		return response;
	}
	
	private void validate(GetTokenRequest request) {

		StringBuilder valid = new StringBuilder();
		
		if (request.getApiEndpoint() == null || request.getApiEndpoint().trim().isEmpty()) {
			valid.append("Missing ApiEndpoint for GetTokenRequest");
			valid.append(System.getProperty("\n"));
		}
		
		if (request.getApiKeyId() == null || request.getApiKeyId().trim().isEmpty()) {
			valid.append("Missing ApiKeyId for GetTokenRequest");
			valid.append(System.getProperty("\n"));
		}
		
		if (valid.length() > 0) {
			throw new IllegalArgumentException(valid.toString());
		}
	}
}