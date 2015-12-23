package com.nowellpoint.aws.client;

import com.nowellpoint.aws.model.LambdaResponseException;
import com.nowellpoint.aws.model.sforce.GetAuthorizationRequest;
import com.nowellpoint.aws.model.sforce.GetAuthorizationResponse;
import com.nowellpoint.aws.model.sforce.GetIdentityRequest;
import com.nowellpoint.aws.model.sforce.GetIdentityResponse;
import com.nowellpoint.aws.model.sforce.GetTokenRequest;
import com.nowellpoint.aws.model.sforce.GetTokenResponse;
import com.nowellpoint.aws.model.sforce.RevokeTokenRequest;
import com.nowellpoint.aws.model.sforce.RevokeTokenResponse;
import com.nowellpoint.aws.model.sforce.CreateSObjectRequest;
import com.nowellpoint.aws.model.sforce.CreateSObjectResponse;

public class SalesforceClient extends AbstractClient {
	
	public SalesforceClient() {
		
	}
	
	public GetTokenResponse authenticate(GetTokenRequest tokenRequest) throws LambdaResponseException {
		return invoke("SalesforceAuthenticationRequest", tokenRequest, GetTokenResponse.class);
	}

	public GetAuthorizationResponse authorize(GetAuthorizationRequest authorizationRequest) throws LambdaResponseException {
		return invoke("SalesforceTokenRequest", authorizationRequest, GetAuthorizationResponse.class);
	}
	
	public GetIdentityResponse getIdentity(GetIdentityRequest identityRequest) throws LambdaResponseException {
		return invoke("SalesforceIdentityRequest", identityRequest, GetIdentityResponse.class);
	}
	
	public RevokeTokenResponse revoke(RevokeTokenRequest revokeTokenRequest) throws LambdaResponseException {
		return invoke("SalesforceRevokeTokenRequest", revokeTokenRequest, RevokeTokenResponse.class);
	}
	
	public CreateSObjectResponse createSObject(CreateSObjectRequest createSObjectRequest) throws LambdaResponseException {
		return invoke("SalesforceCreateSObject", createSObjectRequest, CreateSObjectResponse.class);
	}
}