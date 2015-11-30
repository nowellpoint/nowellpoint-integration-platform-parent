package com.nowellpoint.aws.client;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

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
import com.nowellpoint.aws.model.LambdaResponseException;

public abstract class AbstractClient {
	
	private static AWSLambda lambda = new AWSLambdaClient();
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public AbstractClient() {

	}
	
	protected <T extends AbstractLambdaResponse> T invoke(String functionName, AbstractLambdaRequest request, Class<T> type) throws LambdaResponseException {
		
		String payload = null;
		try {
			payload = objectMapper.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			throw new LambdaResponseException(e);
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
			throw new LambdaResponseException(e);
		}
		
		return object;
	}
	
	protected <T extends AbstractLambdaResponse> T invoke(String functionName, AbstractLambdaRequest request, Class<T> type, Map<String,String> properties) throws LambdaResponseException {
		
		ObjectNode clientContext = objectMapper.createObjectNode();
		clientContext.putNull("client");
		clientContext.putNull("environment");
		clientContext.putObject("custom");
		clientContext.setAll(objectMapper.convertValue(properties, ObjectNode.class));
		
		String payload = null;
		try {
			payload = objectMapper.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			throw new LambdaResponseException(e);
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
			throw new LambdaResponseException(e);
		}
		
		return object;
	}
}