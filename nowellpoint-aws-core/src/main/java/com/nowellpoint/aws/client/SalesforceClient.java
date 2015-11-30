package com.nowellpoint.aws.client;

import java.io.IOException;

import com.nowellpoint.aws.model.sforce.GetAuthorizationRequest;
import com.nowellpoint.aws.model.sforce.GetAuthorizationResponse;
import com.nowellpoint.aws.model.sforce.GetIdentityRequest;
import com.nowellpoint.aws.model.sforce.GetIdentityResponse;
import com.nowellpoint.aws.model.sforce.GetTokenRequest;
import com.nowellpoint.aws.model.sforce.GetTokenResponse;
import com.nowellpoint.aws.model.sforce.RevokeTokenRequest;
import com.nowellpoint.aws.model.sforce.RevokeTokenResponse;

public class SalesforceClient extends AbstractClient {
	
	public SalesforceClient() {
		
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