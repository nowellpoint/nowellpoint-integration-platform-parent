package com.nowellpoint.listener.connection;

import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;

public class LambdaAsyncClient {
	
	private static final AWSLambdaAsync LAMBDA_ASYNC = AWSLambdaAsyncClientBuilder.defaultClient();
	
	private LambdaAsyncClient() {}

	public static AWSLambdaAsync getInstance() {
		return LAMBDA_ASYNC;
	}
}