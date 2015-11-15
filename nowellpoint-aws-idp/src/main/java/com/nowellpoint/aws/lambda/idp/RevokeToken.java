package com.nowellpoint.aws.lambda.idp;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

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

public class RevokeToken implements RequestHandler<IntegrationRequest, Token> {
	
	private static final Logger log = Logger.getLogger(RevokeToken.class.getName());
	private static final String endpoint = "https://api.stormpath.com/v1/accessTokens";

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
			response = RestResource.delete(endpoint)
					.basicAuthorization(Configuration.getStormpathApiKeyId(), Configuration.getStormpathApiKeySecret())
					.path(parseToken(token).getBody().getId())
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
	
	public Jws<Claims> parseToken(String token) throws SignatureException {
		return Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(Configuration.getStormpathApiKeySecret().getBytes()))
				.parseClaimsJws(token);
	}
}