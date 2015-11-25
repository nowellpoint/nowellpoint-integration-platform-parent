package com.nowellpoint.aws.client;

import java.io.IOException;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.AbstractRequest;
import com.nowellpoint.aws.model.AbstractResponse;

public abstract class AbstractClient {
	
	private static AWSLambda lambda = new AWSLambdaClient();
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	protected <T extends AbstractResponse> T invoke(String functionName, AbstractRequest request, Class<T> type) throws IOException {
		InvokeRequest invokeRequest = new InvokeRequest();
		invokeRequest.setInvocationType(InvocationType.RequestResponse);
		invokeRequest.setFunctionName(functionName);
		invokeRequest.setPayload(request.asJson());
		
		InvokeResult invokeResult = lambda.invoke(invokeRequest);
		
		return objectMapper.readValue(invokeResult.getPayload().array(), type);
	}
}