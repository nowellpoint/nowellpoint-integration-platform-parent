package com.nowellpoint.aws.lambda.idp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.idp.Account;
import com.nowellpoint.aws.model.idp.UpdateAccountRequest;
import com.nowellpoint.aws.model.idp.UpdateAccountResponse;

public class UpdateAccount implements RequestHandler<UpdateAccountRequest, UpdateAccountResponse> {
	
	private static LambdaLogger logger;

	@Override
	public UpdateAccountResponse handleRequest(UpdateAccountRequest request, Context context) {
		
		//
		//
		//
		
		logger = context.getLogger();
		
		/**
		 * 
		 */
		
		UpdateAccountResponse response = new UpdateAccountResponse();
		
		/**
		 * 
		 */
		
		try {
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			Account account = new Account();
			account.setEmail(request.getEmail());
			account.setGivenName(request.getGivenName());
			account.setMiddleName(request.getMiddleName());
			account.setSurname(request.getSurname());
			account.setUsername(request.getUsername());
						
			HttpResponse httpResponse = RestResource.post(request.getHref())
					.contentType(MediaType.APPLICATION_JSON)
					.basicAuthorization(request.getApiKeyId(), request.getApiKeySecret())
					.body(objectMapper.writeValueAsString(account))
					.execute();
				
			logger.log("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			/**
			 * 
			 */
							
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == 200) {
				account = httpResponse.getEntity(Account.class);
				response.setAccount(account);
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