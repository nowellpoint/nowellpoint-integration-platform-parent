package com.nowellpoint.aws.lambda.sforce;

import java.io.IOException;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.sforce.CreateSObjectRequest;
import com.nowellpoint.aws.model.sforce.CreateSObjectResponse;

public class CreateSObject implements RequestHandler<CreateSObjectRequest, CreateSObjectResponse> {
	
	private static final Logger log = Logger.getLogger(CreateSObject.class.getName());

	@Override
	public CreateSObjectResponse handleRequest(CreateSObjectRequest request, Context context) { 
		
		/**
		 * 
		 */
		
		CreateSObjectResponse response = new CreateSObjectResponse();
			
		/**
		 * 
		 */
		
		log.info("AccessToken: " + request.getAccessToken());
		log.info("Type: " + request.getType());
		log.info("INstance URL: " + request.getInstanceUrl());
		log.info("SObject: " + request.getSobject());
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(request.getInstanceUrl())
					.path("services/data/v35.0/sobjects/")
					.path(request.getType())
					.header("Content-type", MediaType.APPLICATION_JSON)
					.bearerAuthorization(request.getAccessToken())
					.body(request.getSobject())
					.execute();
			
			log.info("Create SObject status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			/**
			 * 
			 */
			
			response.setStatusCode(httpResponse.getStatusCode());
			
			/**
			 * 
			 */
			
			if (response.getStatusCode() < 400) {	
				JsonNode node = httpResponse.getEntity(JsonNode.class);
				log.info(node.toString());
				if (node.get("success").asBoolean()) {
					response.setId(node.get("id").asText());
				} else {
					response.setErrorMessage(node.get("errors").asText());
				}
			} else {
				//[{"message":"POST requires content-length","errorCode":"UNKNOWN_EXCEPTION"}]
				JsonNode errorResponse = httpResponse.getEntity(JsonNode.class);
				//response.setErrorCode(errorResponse.get("errorCode").asText());
				response.setErrorMessage(errorResponse.toString());
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