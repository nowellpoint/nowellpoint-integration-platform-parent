package com.nowellpoint.aws.service;

import java.io.IOException;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.AbstractResponse;

public abstract class AbstractService {
	
	private static AWSLambda lambda = new AWSLambdaClient();
	private static ObjectMapper objectMapper = new ObjectMapper();

	protected <T extends AbstractResponse> T readInvokeResult(Class<T> type, InvokeResult invokeResult) throws IOException {
		return objectMapper.readValue(invokeResult.getPayload().array(), type);
	}	
	
	protected InvokeResult invoke(InvokeRequest invokeRequest) {
		return lambda.invoke(invokeRequest);
	}
}