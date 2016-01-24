package com.nowellpoint.aws.idp.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.CreateAccountRequest;
import com.nowellpoint.aws.idp.model.CreateAccountResponse;

public class CreateAccount implements RequestHandler<CreateAccountRequest, CreateAccountResponse> {
	
	private static LambdaLogger logger;

	@Override
	public CreateAccountResponse handleRequest(CreateAccountRequest request, Context context) {
		
		//
		//
		//
		
		logger = context.getLogger();
		
		/**
		 * 
		 */
		
		CreateAccountResponse response = new CreateAccountResponse();
		
		/**
		 * 
		 */
		
		try {
			
			Account account = new Account();
			account.setEmail(request.getEmail());
			account.setGivenName(request.getGivenName());
			account.setMiddleName(request.getMiddleName());
			account.setSurname(request.getSurname());
			account.setUsername(request.getUsername());
			account.setPassword(request.getPassword());
						
			HttpResponse httpResponse = RestResource.post(request.getApiEndpoint())
					.contentType(MediaType.APPLICATION_JSON)
					.path("directories")
					.path(request.getDirectoryId())
					.path("accounts")
					.basicAuthorization(request.getApiKeyId(), request.getApiKeySecret())
					.body(account)
					.execute();
				
			logger.log("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			/**
			 * 
			 */
							
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == 201) {
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