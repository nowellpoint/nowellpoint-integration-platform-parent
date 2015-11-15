package com.nowellpoint.aws.lambda.sforce;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.sforce.model.GetIdentityRequest;
import com.nowellpoint.aws.sforce.model.GetIdentityResponse;
import com.nowellpoint.aws.sforce.model.Identity;

public class GetIdentity implements RequestHandler<GetIdentityRequest, GetIdentityResponse> {
	
	private static final Logger log = Logger.getLogger(GetIdentity.class.getName());

	@Override
	public GetIdentityResponse handleRequest(GetIdentityRequest request, Context context) {
		
		/**
		 * 
		 */
		
		GetIdentityResponse response = new GetIdentityResponse();
		
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
			
			log.info("Identity response status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			/**
			 * 
			 */
			
			response.setStatusCode(httpResponse.getStatusCode());
			
			String json = httpResponse.getEntity();
			
			log.info(json);
			
			if (response.getStatusCode() < 400) {		
				response.setIdentity(new ObjectMapper().readValue(json, Identity.class));
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