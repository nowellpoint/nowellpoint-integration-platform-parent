package com.nowellpoint.aws.idp.lambda;

import java.util.UUID;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			Account account = new Account();
			account.setEmail(request.getEmail());
			account.setGivenName(request.getGivenName());
			account.setMiddleName(request.getMiddleName());
			account.setSurname(request.getSurname());
			account.setUsername(request.getUsername());
			
			ObjectNode node = objectMapper.readValue(objectMapper.writeValueAsString(account), ObjectNode.class);
			node.put("password", generatePassword());
			
			HttpResponse httpResponse = RestResource.post(request.getApiEndpoint())
					.contentType(MediaType.APPLICATION_JSON)
					.path("applications")
					.path(request.getApplicationId())
					.path("accounts")
					.basicAuthorization(request.getApiKeyId(), request.getApiKeySecret())
					.body(node)
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
	
	private String generatePassword() {
		String password = UUID.randomUUID().toString().replaceAll("-", "");
		for (int i = 0; i < password.length(); i++) {
			if (Character.isAlphabetic(password.charAt(i))) {
				password = password.replace(password.charAt(i), Character.toUpperCase(password.charAt(i)));
				break;
			}
		}
		return password;
	}
}