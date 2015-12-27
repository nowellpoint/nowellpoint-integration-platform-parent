package com.nowellpoint.aws.client;

import com.nowellpoint.aws.model.LambdaResponseException;
import com.nowellpoint.aws.model.idp.CreateAccountRequest;
import com.nowellpoint.aws.model.idp.CreateAccountResponse;
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
	
	public GetTokenResponse authenticate(GetTokenRequest request) throws LambdaResponseException {		
		return invoke("IdentityProviderUsernamePasswordAuthentication", request, GetTokenResponse.class);
	}
	
	public GetAccountResponse account(GetAccountRequest request) throws LambdaResponseException {		
		return invoke("IdentityProviderGetAccount", request, GetAccountResponse.class);
	}
	
	public CreateAccountResponse account(CreateAccountRequest request) throws LambdaResponseException {
		return invoke("IdentityProviderCreateAccount", request, CreateAccountResponse.class);
	}
	
	public GetCustomDataResponse customData(GetCustomDataRequest request) throws LambdaResponseException {
		return invoke("IdentityProviderGetCustomData", request, GetCustomDataResponse.class);
	}
	
	public VerifyTokenResponse verify(VerifyTokenRequest request) throws LambdaResponseException {
		return invoke("VerifyTokenRequest", request, VerifyTokenResponse.class);
	}
	
	public RefreshTokenResponse refresh(RefreshTokenRequest request) throws LambdaResponseException {
		return invoke("RefreshTokenRequest", request, RefreshTokenResponse.class);
	}
	
	public RevokeTokenResponse revoke(RevokeTokenRequest request) throws LambdaResponseException {
		return invoke("RevokeToken", request, RevokeTokenResponse.class);
	}
}