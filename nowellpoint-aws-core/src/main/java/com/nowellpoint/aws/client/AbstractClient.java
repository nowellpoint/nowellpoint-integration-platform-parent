package com.nowellpoint.aws.client;

import java.io.IOException;
import java.util.Base64;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.model.AbstractLambdaRequest;
import com.nowellpoint.aws.model.AbstractLambdaResponse;

public abstract class AbstractClient {
	
	private static AWSLambda lambda = new AWSLambdaClient();
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public AbstractClient() {

	}
	
	protected <T extends AbstractLambdaResponse> T invoke(String functionName, AbstractLambdaRequest request, Class<T> type) throws IOException {
		
		InvokeRequest invokeRequest = new InvokeRequest();
		invokeRequest.setInvocationType(InvocationType.RequestResponse);
		invokeRequest.setFunctionName(functionName);
		invokeRequest.setPayload(objectMapper.writeValueAsString(request));
		
		InvokeResult invokeResult = lambda.invoke(invokeRequest);
		
		return objectMapper.readValue(invokeResult.getPayload().array(), type);
	}
	
	protected <T extends AbstractLambdaResponse> T invoke(String functionName, AbstractLambdaRequest request, Class<T> type, String accessToken) throws IOException {
		
		ObjectNode clientContext = objectMapper.createObjectNode();
		clientContext.putNull("client");
		clientContext.putNull("environment");
		clientContext.putObject("custom").put("accessToken", accessToken);
		
		InvokeRequest invokeRequest = new InvokeRequest();
		invokeRequest.setInvocationType(InvocationType.RequestResponse);
		invokeRequest.setFunctionName(functionName);
		invokeRequest.setPayload(objectMapper.writeValueAsString(request));
		invokeRequest.setClientContext(Base64.getEncoder().encodeToString(clientContext.toString().getBytes()));
		
		InvokeResult invokeResult = lambda.invoke(invokeRequest);
		
		return objectMapper.readValue(invokeResult.getPayload().array(), type);
	}
}