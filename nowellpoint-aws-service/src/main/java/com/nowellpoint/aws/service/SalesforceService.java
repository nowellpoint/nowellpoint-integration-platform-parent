package com.nowellpoint.aws.service;

import java.io.IOException;

import com.nowellpoint.aws.sforce.model.GetAuthorizationRequest;
import com.nowellpoint.aws.sforce.model.GetAuthorizationResponse;
import com.nowellpoint.aws.sforce.model.GetIdentityRequest;
import com.nowellpoint.aws.sforce.model.GetIdentityResponse;
import com.nowellpoint.aws.sforce.model.GetTokenRequest;
import com.nowellpoint.aws.sforce.model.GetTokenResponse;
import com.nowellpoint.aws.sforce.model.RevokeTokenRequest;
import com.nowellpoint.aws.sforce.model.RevokeTokenResponse;

public class SalesforceService extends AbstractService {
	
	public SalesforceService() {
		
	}
	
	public GetTokenResponse authenticate(GetTokenRequest tokenRequest) throws IOException {
		return invoke("SalesforceAuthenticationRequest", tokenRequest, GetTokenResponse.class);
	}

	public GetAuthorizationResponse authorize(GetAuthorizationRequest authorizationRequest) throws IOException {
		return invoke("SalesforceTokenRequest", authorizationRequest, GetAuthorizationResponse.class);
	}
	
	public GetIdentityResponse getIdentity(GetIdentityRequest identityRequest) throws IOException {
		return invoke("SalesforceIdentityRequest", identityRequest, GetIdentityResponse.class);
	}
	
	public RevokeTokenResponse revoke(RevokeTokenRequest revokeTokenRequest) throws IOException {
		return invoke("SalesforceRevokeTokenRequest", revokeTokenRequest, RevokeTokenResponse.class);
	}
}