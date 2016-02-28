package com.nowellpoint.aws.lambda.sforce;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.sforce.CreateLeadRequest;
import com.nowellpoint.aws.model.sforce.CreateLeadResponse;
import com.nowellpoint.aws.model.sforce.ErrorResponse;

public class CreateLead implements RequestHandler<CreateLeadRequest, CreateLeadResponse> {
	
	private static LambdaLogger logger;

	@Override
	public CreateLeadResponse handleRequest(CreateLeadRequest request, Context context) {
		
		//
		//
		//
		
		logger = context.getLogger();
		
		//
		//
		//
		
		CreateLeadResponse response = new CreateLeadResponse();
		
		//
		//
		//
			
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(request.getInstanceUrl())
					.path("services/apexrest/nowellpoint/lead")
					.header("Content-type", MediaType.APPLICATION_JSON)
					.bearerAuthorization(request.getAccessToken())
					.body(new ObjectMapper().writeValueAsString(request.getLead()))
					.execute();
			
		} catch (IOException e) {
			logger.log(e.getMessage());
			response.setStatusCode(400);
			response.setErrorCode("invalid_request");
			response.setErrorMessage(e.getMessage());
		}
		
		//
		//
		//
		
		logger.log("Create Lead status: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
		
		//
		//
		//
		
		response.setStatusCode(httpResponse.getStatusCode());
		
		//
		//
		//
			
		try {
			
			if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
				response.setId(httpResponse.getAsString());
				logger.log("lead id: " + response.getId());
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

		//
		//
		//
		
		return response;
	}
}