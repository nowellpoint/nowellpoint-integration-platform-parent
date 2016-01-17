package com.nowellpoint.aws.lambda.sforce;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.sforce.CreateSObjectRequest;
import com.nowellpoint.aws.model.sforce.CreateSObjectResponse;
import com.nowellpoint.aws.model.sforce.ErrorResponse;

public class CreateSObject implements RequestHandler<CreateSObjectRequest, CreateSObjectResponse> {
	
	private static LambdaLogger logger;

	@Override
	public CreateSObjectResponse handleRequest(CreateSObjectRequest request, Context context) { 
		
		//
		//
		//
		
		logger = context.getLogger();
		
		/**
		 * 
		 */
		
		CreateSObjectResponse response = new CreateSObjectResponse();
			
		/**
		 * 
		 */
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(request.getInstanceUrl())
					.path("services/data/v35.0/sobjects/")
					.path(request.getType())
					.header("Content-type", MediaType.APPLICATION_JSON)
					.bearerAuthorization(request.getAccessToken())
					.body(request.getSobject())
					.execute();
			
			logger.log("Create SObject status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			/**
			 * 
			 */
			
			response.setStatusCode(httpResponse.getStatusCode());
			
			/**
			 * 
			 */
			
			if (response.getStatusCode() < 400) {	
				JsonNode node = httpResponse.getEntity(JsonNode.class);
				if (node.get("success").asBoolean()) {
					response.setId(node.get("id").asText());
				} else {
					response.setErrorMessage(node.get("errors").asText());
				}
			} else {
				ErrorResponse[] errors = httpResponse.getEntity(ErrorResponse[].class);
				response.setErrorCode(errors[0].getErrorCode());
				response.setErrorMessage(errors[0].getMessage());
				logger.log(response.getErrorCode() + ": " + response.getErrorMessage());
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