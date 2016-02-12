package com.nowellpoint.aws.idp.lambda;

import java.util.Base64;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.RevokeTokenRequest;
import com.nowellpoint.aws.idp.model.RevokeTokenResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class RevokeToken implements RequestHandler<RevokeTokenRequest, RevokeTokenResponse> {

	private static LambdaLogger logger;

	@Override
	public RevokeTokenResponse handleRequest(RevokeTokenRequest request, Context context) { 
		
		//
		//
		//
		
		logger = context.getLogger();
		
		/**
		 * 
		 */
		
		RevokeTokenResponse response = new RevokeTokenResponse();
			
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			
			Jws<Claims> jws = Jwts.parser()
					.setSigningKey(Base64.getUrlEncoder().encodeToString(request.getApiKeySecret().getBytes()))
					.parseClaimsJws(request.getAccessToken());
			
			httpResponse = RestResource.delete(request.getApiEndpoint())
					.path("accessTokens")
					.basicAuthorization(request.getApiKeyId(), request.getApiKeySecret())
					.path(jws.getBody().getId())
					.execute();
			
			logger.log("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
							
			/**
			 * 
			 */
							
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() >= 400) {						
				JsonNode errorResponse = httpResponse.getEntity(JsonNode.class);
				response.setErrorCode(errorResponse.get("message").asText());
				response.setErrorMessage(errorResponse.get("developerMessage").asText());
			}
			
		} catch (Exception e) {
			logger.log(this.getClass().getName().concat(" - ").concat(e.getMessage()));
			response.setStatusCode(400);
			response.setErrorCode("bad_request");
			response.setErrorMessage(e.getMessage());
		}
		
		return response;
	}
}