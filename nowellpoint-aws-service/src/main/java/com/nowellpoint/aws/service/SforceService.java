package com.nowellpoint.aws.service;

import java.io.IOException;

import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.nowellpoint.aws.sforce.model.GetAuthorizationRequest;
import com.nowellpoint.aws.sforce.model.GetAuthorizationResponse;
import com.nowellpoint.aws.sforce.model.GetIdentityRequest;
import com.nowellpoint.aws.sforce.model.GetIdentityResponse;
import com.nowellpoint.aws.sforce.model.GetTokenRequest;
import com.nowellpoint.aws.sforce.model.GetTokenResponse;

public class SforceService extends AbstractService {
	
	public SforceService() {
		
	}
	
	public GetTokenResponse authenticate(GetTokenRequest tokenRequest) throws IOException {
		InvokeRequest invokeRequest = new InvokeRequest();
		invokeRequest.setInvocationType(InvocationType.RequestResponse);
		invokeRequest.setFunctionName("SalesforceAuthenticationRequest");
		invokeRequest.setPayload(tokenRequest.getAsJson());
		
		InvokeResult invokeResult = invoke(invokeRequest);
		
		GetTokenResponse tokenResponse = readInvokeResult(GetTokenResponse.class, invokeResult);
		
		return tokenResponse;
	}

	public GetAuthorizationResponse authorize(GetAuthorizationRequest authorizationRequest) throws IOException {
		InvokeRequest invokeRequest = new InvokeRequest();
		invokeRequest.setInvocationType(InvocationType.RequestResponse);
		invokeRequest.setFunctionName("SalesforceTokenRequest");
		invokeRequest.setPayload(authorizationRequest.getAsJson());
		
		InvokeResult invokeResult = invoke(invokeRequest);
		
		GetAuthorizationResponse authorizationResponse = readInvokeResult(GetAuthorizationResponse.class, invokeResult);
		
		return authorizationResponse;
	}
	
	public GetIdentityResponse getIdentity(GetIdentityRequest identityRequest) throws IOException {
		InvokeRequest invokeRequest = new InvokeRequest();
		invokeRequest.setInvocationType(InvocationType.RequestResponse);
		invokeRequest.setFunctionName("SalesforceIdentityRequest");
		invokeRequest.setPayload(identityRequest.getAsJson());
		
		InvokeResult invokeResult = invoke(invokeRequest);
		
		GetIdentityResponse identityResponse = readInvokeResult(GetIdentityResponse.class, invokeResult);
		
		return identityResponse;
	}
}