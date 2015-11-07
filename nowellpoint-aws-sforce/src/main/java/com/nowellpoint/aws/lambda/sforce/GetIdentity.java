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
import com.nowellpoint.aws.lambda.sforce.model.Identity;
import com.nowellpoint.aws.model.IntegrationRequest;

public class GetIdentity implements RequestHandler<IntegrationRequest, Identity> {
	
	private static final Logger log = Logger.getLogger(GetIdentity.class.getName());

	@Override
	public Identity handleRequest(IntegrationRequest request, Context context) {
		
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
		
		HttpResponse response = null;
		try {
			response = RestResource.get(request.getParameter("id"))
					.acceptCharset(StandardCharsets.UTF_8)
					.bearerAuthorization(authorization)
					.accept(MediaType.APPLICATION_JSON)
					.queryParameter("version", "latest")
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
			
			return response.getEntity(Identity.class);
			
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