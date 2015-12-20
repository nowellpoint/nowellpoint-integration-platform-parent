package com.nowellpoint.aws.client;

import com.nowellpoint.aws.model.LambdaResponseException;
import com.nowellpoint.aws.model.idp.GetAccountRequest;
import com.nowellpoint.aws.model.idp.GetAccountResponse;
import com.nowellpoint.aws.model.idp.GetCustomDataRequest;
import com.nowellpoint.aws.model.idp.GetCustomDataResponse;
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
	
	public GetTokenResponse authenticate(GetTokenRequest tokenRequest) throws LambdaResponseException {		
		return invoke("IdentityProviderUsernamePasswordAuthentication", tokenRequest, GetTokenResponse.class);
	}
	
	public GetAccountResponse account(GetAccountRequest accountRequest) throws LambdaResponseException {		
		return invoke("IdentityProviderGetAccount", accountRequest, GetAccountResponse.class);
	}
	
	public GetCustomDataResponse customData(GetCustomDataRequest customDataRequest) throws LambdaResponseException {
		return invoke("IdentityProviderGetCustomData", customDataRequest, GetCustomDataResponse.class);
	}
	
	public VerifyTokenResponse verify(VerifyTokenRequest verifyTokenRequest) throws LambdaResponseException {
		return invoke("VerifyTokenRequest", verifyTokenRequest, VerifyTokenResponse.class);
	}
	
	public RefreshTokenResponse refresh(RefreshTokenRequest refreshTokenRequest) throws LambdaResponseException {
		return invoke("RefreshTokenRequest", refreshTokenRequest, RefreshTokenResponse.class);
	}
	
	public RevokeTokenResponse revoke(RevokeTokenRequest revokeTokenRequest) throws LambdaResponseException {
		return invoke("RevokeToken", revokeTokenRequest, RevokeTokenResponse.class);
	}
}