package com.nowellpoint.aws.lambda.idp;

import java.io.IOException;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.RefreshTokenRequest;
import com.nowellpoint.aws.idp.model.RefreshTokenResponse;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.aws.util.Configuration;

public class RefreshToken implements RequestHandler<RefreshTokenRequest, RefreshTokenResponse> {
	
	private static final Logger log = Logger.getLogger(RefreshToken.class.getName());
	private static final String endpoint = "https://api.stormpath.com/v1/applications";

	@Override
	public RefreshTokenResponse handleRequest(RefreshTokenRequest request, Context context) { 
		
		/**
		 * 
		 */
		
		RefreshTokenResponse response = new RefreshTokenResponse();
			
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(endpoint)
					.path(Configuration.getStormpathApplicationId())
					.path("oauth/token")
					.basicAuthorization(Configuration.getStormpathApiKeyId(), Configuration.getStormpathApiKeySecret())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.parameter("grant_type", "refresh_token")
					.parameter("refresh_token", request.getRefreshToken())
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