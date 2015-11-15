package com.nowellpoint.aws.service;

import java.io.IOException;

import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.nowellpoint.aws.idp.model.GetTokenRequest;
import com.nowellpoint.aws.idp.model.GetTokenResponse;

public class IdentityProviderService extends AbstractService {

	public IdentityProviderService() {
		
	}
	
	public GetTokenResponse authenticate(GetTokenRequest tokenRequest) throws IOException {
		InvokeRequest invokeRequest = new InvokeRequest();
		invokeRequest.setInvocationType(InvocationType.RequestResponse);
		invokeRequest.setFunctionName("IDP_UsernamePasswordAuthentication");
		invokeRequest.setPayload(tokenRequest.getAsJson());
		
		InvokeResult invokeResult = invoke(invokeRequest);
		
		GetTokenResponse tokenResponse = readInvokeResult(GetTokenResponse.class, invokeResult);
		
		return tokenResponse;
	}
}