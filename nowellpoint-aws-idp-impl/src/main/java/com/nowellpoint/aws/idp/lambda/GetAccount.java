package com.nowellpoint.aws.idp.lambda;

import java.time.Instant;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.GetAccountRequest;
import com.nowellpoint.aws.idp.model.GetAccountResponse;

public class GetAccount implements RequestHandler<GetAccountRequest, GetAccountResponse> {
	
	private static LambdaLogger logger;

	@Override
	public GetAccountResponse handleRequest(GetAccountRequest request, Context context) {
		
		/**
		 * 
		 */
		
		long startTime = System.currentTimeMillis();
		
		/**
		 * 
		 */
		
		logger = context.getLogger();
		
		/**
		 * 
		 */
		
		GetAccountResponse response = new GetAccountResponse();
		
		/**
		 * 
		 */
		
		try {
			
			/**
			 * 
			 */
			
			HttpResponse httpResponse = RestResource.get(request.getHref())
					.basicAuthorization(request.getApiKeyId(), request.getApiKeySecret())
					.execute();
				
			logger.log("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());
			
			/**
			 * 
			 */
							
			response.setStatusCode(httpResponse.getStatusCode());
			
			if (httpResponse.getStatusCode() == 200) {
				Account account = httpResponse.getEntity(Account.class);
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
		
		logger.log(Instant.now() + " " + context.getAwsRequestId() + " execution time: " + (System.currentTimeMillis() - startTime));
		
		return response;
	}
}