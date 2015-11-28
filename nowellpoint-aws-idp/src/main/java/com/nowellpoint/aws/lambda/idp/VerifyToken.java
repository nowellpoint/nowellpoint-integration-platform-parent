package com.nowellpoint.aws.lambda.idp;

import java.io.IOException;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.idp.AuthToken;
import com.nowellpoint.aws.model.idp.VerifyTokenRequest;
import com.nowellpoint.aws.model.idp.VerifyTokenResponse;

public class VerifyToken implements RequestHandler<VerifyTokenRequest, VerifyTokenResponse> {
	
	private static final Logger log = Logger.getLogger(VerifyToken.class.getName());

	@Override
	public VerifyTokenResponse handleRequest(VerifyTokenRequest request, Context context) { 
		
		/**
		 * 
		 */
		
		VerifyTokenResponse response = new VerifyTokenResponse();
			
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.get(Configuration.getStormpathApiEndpoint())
					.path("applications")
					.basicAuthorization(Configuration.getStormpathApiKeyId(), Configuration.getStormpathApiKeySecret())
					.path(Configuration.getStormpathApplicationId())
					.path("authTokens")
					.path(request.getAccessToken())
					.execute();
				
			log.info("Status Code: " + response.getStatusCode() + " Target: " + httpResponse.getURL());
							
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
			log.severe(e.getMessage());
			response.setStatusCode(400);
			response.setErrorCode("invalid_request");
			response.setErrorMessage(e.getMessage());
		}
		
		return response;
	}
}