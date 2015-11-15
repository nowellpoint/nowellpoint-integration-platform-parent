package com.nowellpoint.aws.service;

import java.io.IOException;

import com.nowellpoint.aws.idp.model.GetTokenRequest;
import com.nowellpoint.aws.idp.model.GetTokenResponse;
import com.nowellpoint.aws.idp.model.RefreshTokenRequest;
import com.nowellpoint.aws.idp.model.RefreshTokenResponse;
import com.nowellpoint.aws.idp.model.RevokeTokenRequest;
import com.nowellpoint.aws.idp.model.RevokeTokenResponse;
import com.nowellpoint.aws.idp.model.VerifyTokenRequest;
import com.nowellpoint.aws.idp.model.VerifyTokenResponse;

public class IdentityProviderService extends AbstractService {

	public IdentityProviderService() {
		
	}
	
	public GetTokenResponse authenticate(GetTokenRequest tokenRequest) throws IOException {
		return invoke("IDP_UsernamePasswordAuthentication", tokenRequest, GetTokenResponse.class);
	}
	
	public VerifyTokenResponse verify(VerifyTokenRequest verifyTokenRequest) throws IOException {
		return invoke("VerifyTokenRequest", verifyTokenRequest, VerifyTokenResponse.class);
	}
	
	public RefreshTokenResponse refresh(RefreshTokenRequest refreshTokenRequest) throws IOException {
		return invoke("RefreshTokenRequest", refreshTokenRequest, RefreshTokenResponse.class);
	}
	
	public RevokeTokenResponse revoke(RevokeTokenRequest revokeTokenRequest) throws IOException {
		return invoke("RevokeToken", revokeTokenRequest, RevokeTokenResponse.class);
	}
}