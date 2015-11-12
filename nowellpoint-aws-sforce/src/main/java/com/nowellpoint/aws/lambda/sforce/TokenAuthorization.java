package com.nowellpoint.aws.lambda.sforce;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import com.amazonaws.AmazonServiceException.ErrorType;
import com.amazonaws.services.apigateway.model.BadRequestException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.lambda.sforce.model.GetAuthorizationRequest;
import com.nowellpoint.aws.lambda.sforce.model.Token;
import com.nowellpoint.aws.util.Configuration;

public class TokenAuthorization implements RequestHandler<GetAuthorizationRequest, Token> {
	
	private static final Logger log = Logger.getLogger(TokenAuthorization.class.getName());

	@Override
	public Token handleRequest(GetAuthorizationRequest request, Context context) { 
		
		/**
		 * 
		 */
		
		log.info(Configuration.getRedirectUri());
		
		HttpResponse response = null;
		try {
			response = RestResource.post(Configuration.getSalesforceTokenUri())
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.contentType("application/x-www-form-urlencoded")
					.parameter("grant_type", "authorization_code")
					.parameter("code", request.getCode())
					.parameter("client_id", Configuration.getSalesforceClientId())
					.parameter("client_secret", Configuration.getSalesforceClientSecret())
					.parameter("redirect_uri", Configuration.getRedirectUri())
					.execute();
			
			log.info("Identity response status: " + response.getStatusCode() + " Target: " + response.getURL());
				
			/**
			 * 
			 */
				
			if (response.getStatusCode() != 200) {		
				log.severe(response.getEntity());
				JsonNode errorResponse = response.getEntity(JsonNode.class);
				BadRequestException exception = new BadRequestException(errorResponse.get("error_description").asText());
				exception.setStatusCode(401);
				exception.setErrorType(ErrorType.Client);
				exception.setRequestId(context.getAwsRequestId());
				exception.setServiceName(context.getFunctionName());
				exception.setErrorCode(errorResponse.get("error").asText());
				throw exception;
			}
			
			return response.getEntity(Token.class);
			
		} catch (IOException e) {
			BadRequestException exception = new BadRequestException(e.getMessage());
			exception.setStatusCode(400);
			exception.setErrorType(ErrorType.Client);
			exception.setRequestId(context.getAwsRequestId());
			exception.setServiceName(context.getFunctionName());
			exception.setErrorCode("INVALID_REQUEST");
			throw exception;
		}
	}
}