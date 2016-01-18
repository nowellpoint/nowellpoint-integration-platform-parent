package com.nowellpoint.aws.lambda.idp;

import java.util.Base64;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.CustomData;
import com.nowellpoint.aws.idp.model.GetCustomDataRequest;
import com.nowellpoint.aws.idp.model.GetCustomDataResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class GetCustomData implements RequestHandler<GetCustomDataRequest, GetCustomDataResponse> {
	
	private static LambdaLogger logger;

	@Override
	public GetCustomDataResponse handleRequest(GetCustomDataRequest request, Context context) {
		
		/**
		 * 
		 */
		
		logger = context.getLogger();
		
		/**
		 * 
		 */
		
		GetCustomDataResponse response = new GetCustomDataResponse();
		
		/**
		 * 
		 */
		
		Jws<Claims> jws = Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(request.getApiKeySecret().getBytes()))
				.parseClaimsJws(request.getAccessToken());
		
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.get(jws.getBody().getSubject())
					.path("customData")
					.basicAuthorization(request.getApiKeyId(), request.getApiKeySecret())
					.execute();
				
			logger.log("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			/**
			 * 
			 */
							
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == 200) {						
				response.setCustomData(httpResponse.getEntity(CustomData.class));
			} else {
				JsonNode errorResponse = httpResponse.getEntity(JsonNode.class);
				response.setErrorCode(errorResponse.get("message").asText());
				response.setErrorMessage(errorResponse.get("developerMessage").asText());
			}
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			response.setStatusCode(400);
			response.setErrorCode("invalid_request");
			response.setErrorMessage(e.getMessage());
		}
		
		return response;
	}
}