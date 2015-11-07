package com.nowellpoint.aws.lambda.sforce;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import com.amazonaws.AmazonServiceException.ErrorType;
import com.amazonaws.services.apigateway.model.BadRequestException;
import com.amazonaws.services.apigateway.model.UnauthorizedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.lambda.sforce.model.Token;
import com.nowellpoint.aws.model.IntegrationRequest;
import com.nowellpoint.aws.util.Configuration;

public class UsernamePasswordAuthentication implements RequestHandler<IntegrationRequest, Token> {
	
	private static final Logger log = Logger.getLogger(UsernamePasswordAuthentication.class.getName());

	@Override
	public Token handleRequest(IntegrationRequest request, Context context) { 
		
		/**
		 * 
		 */
		
		if (request.getParameter("username") == null || request.getParameter("password") == null) {
			BadRequestException exception = new BadRequestException("Request must include username and password");
			exception.setStatusCode(400);
			exception.setErrorType(ErrorType.Client);
			exception.setRequestId(context.getAwsRequestId());
			exception.setServiceName(context.getFunctionName());
			exception.setErrorCode("INVALID_REQUEST");
			throw exception;
		}
			
		/**
		 * 
		 */
		
		HttpResponse response = null;
		try {
			response = RestResource.post(Configuration.getSalesforceTokenUri())
					.contentType("application/x-www-form-urlencoded")
					.accept(MediaType.APPLICATION_JSON)
					.acceptCharset(StandardCharsets.UTF_8)
					.parameter("grant_type", "password")
					.parameter("client_id", Configuration.getSalesforceClientId())
					.parameter("client_secret", Configuration.getSalesforceClientSecret())
					.parameter("username", request.getParameter("username"))
					.parameter("password", request.getParameter("password"))
					.execute();
			
			log.info("Status Code: " + response.getStatusCode() + " Target: " + response.getURL());			
			
			/**
			 * 
			 */
				
			if (response.getStatusCode() != 200) {		
				JsonNode errorResponse = response.getEntity(JsonNode.class);
				UnauthorizedException exception = new UnauthorizedException(errorResponse.get("error_description").asText());
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