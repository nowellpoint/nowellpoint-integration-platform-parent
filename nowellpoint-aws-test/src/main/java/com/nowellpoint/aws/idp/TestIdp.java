package com.nowellpoint.aws.idp;

public class TestIdp {
	
	//private static AWSLambda lambda = new AWSLambdaClient(new EnvironmentVariableCredentialsProvider());

	public void main() {
//		long start = System.currentTimeMillis();
//		
//		String payload = JsonNodeFactory.instance.objectNode()
//				.put("username", System.getenv("STORMPATH_USERNAME"))
//				.put("password", System.getenv("STORMPATH_PASSWORD"))
//				.toString();
//		
//		InvokeRequest invokeRequest = new InvokeRequest();
//		invokeRequest.setInvocationType(InvocationType.RequestResponse);
//		invokeRequest.setFunctionName("IDP_UsernamePasswordAuthentication");
//		invokeRequest.setPayload(payload);
//		
//		InvokeResult invokeResult = lambda.invoke(invokeRequest);
//		
//		System.out.println("Result: " + new String(invokeResult.getPayload().array(), Charset.forName("UTF-8")));
//		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
	}
}