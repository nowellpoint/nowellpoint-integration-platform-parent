package com.nowellpoint.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.invoke.LambdaFunction;

public interface PropertyService {
	@LambdaFunction(functionName="PropertyServiceHandler")
	Map<String,String> getProperties(Map<String,String> propertyStore);
}