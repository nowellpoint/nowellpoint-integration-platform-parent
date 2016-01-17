package com.nowellpoint.aws.client;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.model.AbstractLambdaRequest;
import com.nowellpoint.aws.model.AbstractLambdaResponse;
import com.nowellpoint.aws.model.ClientException;

public abstract class AbstractClient {
	
	private static AWSLambda lambda = new AWSLambdaClient();
	private static ObjectMapper objectMapper = new ObjectMapper();
	protected static Validator validator;
	
	public AbstractClient() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	protected <T extends AbstractLambdaResponse> T invoke(String functionName, AbstractLambdaRequest request, Class<T> type) throws ClientException {
		
		validate( request );
		
		String payload = null;
		try {
			payload = objectMapper.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			throw new ClientException(e);
		}
		
		InvokeRequest invokeRequest = new InvokeRequest();
		invokeRequest.setInvocationType(InvocationType.RequestResponse);
		invokeRequest.setFunctionName(functionName);
		invokeRequest.setPayload(payload);
		
		InvokeResult invokeResult = lambda.invoke(invokeRequest);
		
		T object = null;
		
		try {
			object = objectMapper.readValue(invokeResult.getPayload().array(), type);
		} catch (IOException e) {
			throw new ClientException(e);
		}
		
		return object;
	}
	
	protected <T extends AbstractLambdaResponse> T invoke(String functionName, AbstractLambdaRequest request, Class<T> type, Map<String,String> properties) throws ClientException {
		
		validate( request );
		
		ObjectNode clientContext = objectMapper.createObjectNode();
		clientContext.putNull("client");
		clientContext.putNull("environment");
		clientContext.putObject("custom");
		clientContext.setAll(objectMapper.convertValue(properties, ObjectNode.class));
		
		String payload = null;
		try {
			payload = objectMapper.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			throw new ClientException(e);
		}
		
		InvokeRequest invokeRequest = new InvokeRequest();
		invokeRequest.setInvocationType(InvocationType.RequestResponse);
		invokeRequest.setFunctionName(functionName);
		invokeRequest.setPayload(payload);
		invokeRequest.setClientContext(Base64.getEncoder().encodeToString(clientContext.asText().getBytes()));
		
		InvokeResult invokeResult = lambda.invoke(invokeRequest);
		
		T object = null;
		
		try {
			object = objectMapper.readValue(invokeResult.getPayload().array(), type);
		} catch (IOException e) {
			throw new ClientException(e);
		}
		
		return object;
	}
	
	private <T extends AbstractLambdaRequest> void validate(AbstractLambdaRequest request) throws ClientException {
		Set<ConstraintViolation<AbstractLambdaRequest>> constraintViolations = validator.validate( request );
		if ( ! constraintViolations.isEmpty() ) {
			StringBuilder exceptionMessage = new StringBuilder("Validation Exceptions: ")
					.append( System.getProperty( "line.separator" ) );
			
			constraintViolations.stream().forEach( violation -> exceptionMessage.append( "    " )
					.append( violation.getPropertyPath() )
					.append( " " )
					.append( violation.getMessage() )
					.append( System.getProperty( "line.separator" ) ) );
			throw new ClientException(exceptionMessage.toString());
        }
	}
}