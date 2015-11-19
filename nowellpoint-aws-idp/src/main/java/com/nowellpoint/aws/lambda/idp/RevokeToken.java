package com.nowellpoint.aws.lambda.idp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.idp.RevokeTokenRequest;
import com.nowellpoint.aws.model.idp.RevokeTokenResponse;
import com.nowellpoint.aws.util.Configuration;

public class RevokeToken implements RequestHandler<RevokeTokenRequest, RevokeTokenResponse> {
	
	private static final Logger log = Logger.getLogger(RevokeToken.class.getName());
	private static final String endpoint = "https://api.stormpath.com/v1/accessTokens";

	@Override
	public RevokeTokenResponse handleRequest(RevokeTokenRequest request, Context context) { 
		
		/**
		 * 
		 */
		
		RevokeTokenResponse response = new RevokeTokenResponse();
			
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.delete(endpoint)
					.basicAuthorization(Configuration.getStormpathApiKeyId(), Configuration.getStormpathApiKeySecret())
					.path(parseToken(request.getAccessToken()).getBody().getId())
					.execute();
			
			log.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
							
			/**
			 * 
			 */
							
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() >= 400) {						
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
	
	public Jws<Claims> parseToken(String token) throws SignatureException {
		return Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(Configuration.getStormpathApiKeySecret().getBytes()))
				.parseClaimsJws(token);
	}
}