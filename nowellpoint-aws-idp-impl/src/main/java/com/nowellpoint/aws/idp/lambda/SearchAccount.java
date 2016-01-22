package com.nowellpoint.aws.idp.lambda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.nowellpoint.aws.idp.model.SearchAccountRequest;
import com.nowellpoint.aws.idp.model.SearchAccountResponse;

public class SearchAccount implements RequestHandler<SearchAccountRequest, SearchAccountResponse> {
	
	private static LambdaLogger logger;

	@Override
	public SearchAccountResponse handleRequest(SearchAccountRequest request, Context context) {
		
		//
		//
		//
		
		logger = context.getLogger();
		
		/**
		 * 
		 */
		
		SearchAccountResponse response = new SearchAccountResponse();
		
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
			
			String query = buildQueryString(account);
			
			HttpResponse httpResponse = RestResource.get(request.getApiEndpoint())
					.accept(MediaType.APPLICATION_JSON)
					.path("directories")
					.path(request.getDirectoryId())
					.path("accounts")
					.path(query.toString())
					.basicAuthorization(request.getApiKeyId(), request.getApiKeySecret())
					.execute();
				
			logger.log("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			/**
			 * 
			 */
							
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == 200) {
				ObjectNode successResponse = httpResponse.getEntity(ObjectNode.class);
				response.setSize(successResponse.get("size").asInt());
				response.setHref(successResponse.get("href").asText());
				response.setItems(objectMapper.readValue(successResponse.get("items").toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, Account.class)));
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
	
	private String buildQueryString(Account account) {
		
		Map<String,String> queryParams = new HashMap<String,String>();
		
		if (Optional.ofNullable(account.getUsername()).isPresent()) {
			queryParams.put("username", account.getUsername());
		}
		
		if (Optional.ofNullable(account.getEmail()).isPresent()) {
			queryParams.put("email", account.getEmail());
		}
		
		if (Optional.ofNullable(account.getGivenName()).isPresent()) {
			queryParams.put("givenName", account.getGivenName());
		}
		
		if (Optional.ofNullable(account.getSurname()).isPresent()) {
			queryParams.put("surname", account.getSurname());
		}
		
		if (Optional.ofNullable(account.getMiddleName()).isPresent()) {
			queryParams.put("middleName", account.getMiddleName());
		}
		
		if (Optional.ofNullable(account.getStatus()).isPresent()) {
			queryParams.put("status", account.getStatus());
		}

		StringBuilder query = new StringBuilder("?");
			
		queryParams.entrySet().stream().map(entry -> query.append( entry.getKey() ).append("=")
				.append( queryParams.get( entry.getKey() ) ) ).collect( Collectors.joining (",") );
		
		return query.toString();
	}
}