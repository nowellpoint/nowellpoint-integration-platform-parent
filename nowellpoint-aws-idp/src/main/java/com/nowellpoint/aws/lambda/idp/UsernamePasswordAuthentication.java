package com.nowellpoint.aws.lambda.idp;

import java.io.IOException;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import com.amazonaws.AmazonServiceException.ErrorType;
import com.amazonaws.services.apigateway.model.BadRequestException;
import com.amazonaws.services.apigateway.model.UnauthorizedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.http.Configuration;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.lambda.idp.model.Token;
import com.nowellpoint.aws.http.IntegrationRequest;

public class UsernamePasswordAuthentication implements RequestHandler<IntegrationRequest, Token> {
	
	private static final Logger log = Logger.getLogger(UsernamePasswordAuthentication.class.getName());
	private static final String endpoint = "https://api.stormpath.com/v1/applications";

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
			response = RestResource.post(endpoint)
					.path(Configuration.getStormpathApplicationId())
					.path("oauth/token")
					.basicAuthorization(Configuration.getStormpathApiKeyId(), Configuration.getStormpathApiKeySecret())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.parameter("grant_type", "password")
					.parameter("username", request.getParameter("username"))
					.parameter("password", request.getParameter("password"))
					.execute();
			
			log.info("Status Code: " + response.getStatusCode() + " Target: " + response.getURL());			
			
			/**
			 * 
			 */
				
			if (response.getStatusCode() != 200) {		
				ObjectNode errorResponse = response.getEntity(ObjectNode.class);
				log.severe(errorResponse.toString());
				UnauthorizedException exception = new UnauthorizedException(errorResponse.get("message").asText());
				exception.setStatusCode(errorResponse.get("status").asInt());
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