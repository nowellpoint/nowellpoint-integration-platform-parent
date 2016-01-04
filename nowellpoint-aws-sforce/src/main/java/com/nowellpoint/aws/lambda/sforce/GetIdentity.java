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
import com.nowellpoint.aws.model.sforce.GetIdentityRequest;
import com.nowellpoint.aws.model.sforce.GetIdentityResponse;
import com.nowellpoint.aws.model.sforce.Identity;

public class GetIdentity implements RequestHandler<GetIdentityRequest, GetIdentityResponse> {

	@Override
	public GetIdentityResponse handleRequest(GetIdentityRequest request, Context context) {
		
		/**
		 * 
		 */
		
		LambdaLogger logger = context.getLogger();
		
		/**
		 * 
		 */
		
		GetIdentityResponse response = new GetIdentityResponse();
		
		logger.log("Access Token: " + request.getAccessToken());
		logger.log("Id: " + request.getId());
		
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.get(request.getId())
					.acceptCharset(StandardCharsets.UTF_8)
					.bearerAuthorization(request.getAccessToken())
					.accept(MediaType.APPLICATION_JSON)
					.queryParameter("version", "latest")
					.execute();
			
			logger.log("Identity response status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			/**
			 * 
			 */
			
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (response.getStatusCode() < 400) {		
				response.setIdentity(httpResponse.getEntity(Identity.class));
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