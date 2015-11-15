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
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.model.IntegrationRequest;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.aws.util.Configuration;

public class RefreshToken implements RequestHandler<IntegrationRequest, Token> {
	
	private static final Logger log = Logger.getLogger(RefreshToken.class.getName());
	private static final String endpoint = "https://api.stormpath.com/v1/applications";

	@Override
	public Token handleRequest(IntegrationRequest request, Context context) { 
		
		/**
		 * 
		 */
		
		String authorization = request.getHeaders().getAuthorization();
		
		if (authorization == null) {
			BadRequestException exception = new BadRequestException("Request must include an Authorization header with Bearer and token");
			exception.setStatusCode(401);
			exception.setErrorType(ErrorType.Client);
			exception.setRequestId(context.getAwsRequestId());
			exception.setServiceName(context.getFunctionName());
			exception.setErrorCode("INVALID_REQUEST");
			throw exception;
		}
		
		/**
	     * 
	     */

		String token = authorization.replaceFirst("Bearer", "").trim();
			
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
					.parameter("grant_type", "refresh_token")
					.parameter("refresh_token", token)
					.execute();
			
			log.info("Status Code: " + response.getStatusCode() + " Target: " + response.getURL());
							
			if (response.getStatusCode() != 200) {		
				ObjectNode errorResponse = response.getEntity(ObjectNode.class);
				log.severe(errorResponse.toString());
				UnauthorizedException exception = new UnauthorizedException(errorResponse.get("developerMessage").asText());
				exception.setStatusCode(errorResponse.get("status").asInt());
				exception.setErrorType(ErrorType.Client);
				exception.setRequestId(context.getAwsRequestId());
				exception.setServiceName(context.getFunctionName());
				exception.setErrorCode(errorResponse.get("message").asText());
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