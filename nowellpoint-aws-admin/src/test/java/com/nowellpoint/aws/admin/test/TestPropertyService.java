package com.nowellpoint.aws.admin.test;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

import org.junit.Test;

public class TestPropertyService {

	@Test
	public void testGetProperties() {
		
		AWSLambda lambdaClient = new AWSLambdaClient(new EnvironmentVariableCredentialsProvider());
		lambdaClient.setRegion(Region.getRegion(Regions.US_EAST_1));
		
		InvokeRequest invokeRequest = new InvokeRequest();
        invokeRequest.setFunctionName("PropertyServiceHandler");
        invokeRequest.setPayload("{\"propertyStore\" : \"SANDBOX\"}");
		
        InvokeResult invokeResult = lambdaClient.invoke(invokeRequest);
        String properties = new String(invokeResult.getPayload().array());
        
        System.out.println(properties);
        
	}
}