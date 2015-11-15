package com.nowellpoint.aws.lambda.sforce;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.sforce.model.GetAuthorizationRequest;
import com.nowellpoint.aws.sforce.model.GetAuthorizationResponse;
import com.nowellpoint.aws.sforce.model.Token;
import com.nowellpoint.aws.util.Configuration;

public class TokenAuthorization implements RequestHandler<GetAuthorizationRequest, GetAuthorizationResponse> {
	
	private static final Logger log = Logger.getLogger(TokenAuthorization.class.getName());

	@Override
	public GetAuthorizationResponse handleRequest(GetAuthorizationRequest request, Context context) {
		
		/**
		 * 
		 */
		
		GetAuthorizationResponse response = new GetAuthorizationResponse();
		
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(Configuration.getSalesforceTokenUri())
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.contentType("application/x-www-form-urlencoded")
					.parameter("grant_type", "authorization_code")
					.parameter("code", request.getCode())
					.parameter("client_id", Configuration.getSalesforceClientId())
					.parameter("client_secret", Configuration.getSalesforceClientSecret())
					.parameter("redirect_uri", Configuration.getRedirectUri())
					.execute();
			
			log.info("Identity response status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
				
			/**
			 * 
			 */
			
			response.setStatusCode(httpResponse.getStatusCode());
				
			if (response.getStatusCode() < 400) {		
				response.setToken(httpResponse.getEntity(Token.class));
			} else {
				JsonNode errorResponse = httpResponse.getEntity(JsonNode.class);
				response.setErrorCode(errorResponse.get("error").asText());
				response.setErrorMessage(errorResponse.get("error_description").asText());
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