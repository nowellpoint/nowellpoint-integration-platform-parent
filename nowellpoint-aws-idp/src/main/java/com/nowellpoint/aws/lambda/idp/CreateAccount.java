package com.nowellpoint.aws.lambda.idp;

import java.time.Instant;
import java.util.UUID;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.idp.Account;
import com.nowellpoint.aws.model.idp.CreateAccountRequest;
import com.nowellpoint.aws.model.idp.CreateAccountResponse;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

public class CreateAccount implements RequestHandler<CreateAccountRequest, CreateAccountResponse> {
	
	private static final Logger log = Logger.getLogger(CreateAccount.class.getName());

	@Override
	public CreateAccountResponse handleRequest(CreateAccountRequest request, Context context) {
		
		/**
		 * 
		 */
		
		long startTime = System.currentTimeMillis();
		
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
			
			HttpResponse httpResponse = RestResource.post(Configuration.getStormpathApiEndpoint())
					.contentType(MediaType.APPLICATION_JSON)
					.path("applications")
					.path(Configuration.getStormpathApplicationId())
					.path("accounts")
					.basicAuthorization(request.getApiKeyId(), request.getApiKeySecret())
					.body(node)
					.execute();
				
			log.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			/**
			 * 
			 */
							
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == 201) {
				account = httpResponse.getEntity(Account.class);
				response.setAccount(account);
				
				//
				// send email
				//
				
				String text = new StringBuilder().append("Username: ")
						.append(account.getUsername())
						.append("\n")
						.append("Password: ")
						.append(node.get("password").asText())
						.toString();
				
				SendGrid.Email email = new SendGrid.Email();
				email.addToName(account.getFullName());
				email.addTo(account.getEmail());
				email.setFrom("administrator@nowellpoint.com");
				email.setSubject("Welcome to Nowellpoint!");
				email.setText(text);
				
				SendGrid sendgrid = new SendGrid(Configuration.getSendGridApiKey());

				try {
					sendgrid.send(email);
				} catch (SendGridException e) {
					log.severe(e.getMessage());
				}
				
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
		
		log.info(Instant.now() + " " + context.getAwsRequestId() + " execution time: " + (System.currentTimeMillis() - startTime));
		
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