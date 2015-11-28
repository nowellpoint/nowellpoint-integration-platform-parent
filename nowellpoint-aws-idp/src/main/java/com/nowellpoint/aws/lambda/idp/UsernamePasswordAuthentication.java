package com.nowellpoint.aws.lambda.idp;

import java.io.IOException;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.idp.GetTokenRequest;
import com.nowellpoint.aws.model.idp.GetTokenResponse;
import com.nowellpoint.aws.model.idp.Token;

public class UsernamePasswordAuthentication implements RequestHandler<GetTokenRequest, GetTokenResponse> {
	
	private static final Logger log = Logger.getLogger(UsernamePasswordAuthentication.class.getName());

	@Override
	public GetTokenResponse handleRequest(GetTokenRequest request, Context context) { 
			
		/**
		 * 
		 */
		
		GetTokenResponse response = new GetTokenResponse();
		
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(Configuration.getStormpathApiEndpoint())
					.path("applications")
					.path(Configuration.getStormpathApplicationId())
					.path("oauth/token")
					.basicAuthorization(Configuration.getStormpathApiKeyId(), Configuration.getStormpathApiKeySecret())
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
}