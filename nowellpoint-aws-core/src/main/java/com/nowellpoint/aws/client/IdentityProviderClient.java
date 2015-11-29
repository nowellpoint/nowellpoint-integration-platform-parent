package com.nowellpoint.aws.client;

import java.io.IOException;

import com.nowellpoint.aws.model.idp.GetTokenRequest;
import com.nowellpoint.aws.model.idp.GetTokenResponse;
import com.nowellpoint.aws.model.idp.RefreshTokenRequest;
import com.nowellpoint.aws.model.idp.RefreshTokenResponse;
import com.nowellpoint.aws.model.idp.RevokeTokenRequest;
import com.nowellpoint.aws.model.idp.RevokeTokenResponse;
import com.nowellpoint.aws.model.idp.VerifyTokenRequest;
import com.nowellpoint.aws.model.idp.VerifyTokenResponse;

public class IdentityProviderClient extends AbstractClient {

	public IdentityProviderClient() {
		
	}
	
	public GetTokenResponse authenticate(GetTokenRequest tokenRequest) throws IOException {
		return invoke("IdentityProviderUsernamePasswordAuthentication", tokenRequest, GetTokenResponse.class);
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

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
}