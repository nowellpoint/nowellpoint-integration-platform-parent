package com.nowellpoint.aws.client;

import com.nowellpoint.aws.model.ClientException;
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
import com.nowellpoint.aws.model.idp.SearchAccountRequest;
import com.nowellpoint.aws.model.idp.SearchAccountResponse;
import com.nowellpoint.aws.model.idp.UpdateAccountRequest;
import com.nowellpoint.aws.model.idp.UpdateAccountResponse;
import com.nowellpoint.aws.model.idp.VerifyTokenRequest;
import com.nowellpoint.aws.model.idp.VerifyTokenResponse;

public class IdentityProviderClient extends AbstractClient {

	public IdentityProviderClient() {
		
	}
	
	public GetTokenResponse authenticate(GetTokenRequest getTokenRequest) throws ClientException {		
		return invoke("IdentityProviderUsernamePasswordAuthentication", getTokenRequest, GetTokenResponse.class);
	}
	
	public SearchAccountResponse search(SearchAccountRequest searchAccountRequest) throws ClientException {		
		return invoke("IdentityProviderSearchAccount", searchAccountRequest, SearchAccountResponse.class);
	}
	
	public GetAccountResponse account(GetAccountRequest request) throws ClientException {		
		return invoke("IdentityProviderGetAccount", request, GetAccountResponse.class);
	}
	
	public CreateAccountResponse account(CreateAccountRequest createAccountRequest) throws ClientException {
		return invoke("IdentityProviderCreateAccount", createAccountRequest, CreateAccountResponse.class);
	}
	
	public UpdateAccountResponse account(UpdateAccountRequest updateAccountRequest) throws ClientException {
		return invoke("IdentityProviderUpdateAccount", updateAccountRequest, UpdateAccountResponse.class);
	}
	
	public GetCustomDataResponse customData(GetCustomDataRequest request) throws ClientException {
		return invoke("IdentityProviderGetCustomData", request, GetCustomDataResponse.class);
	}
	
	public VerifyTokenResponse verify(VerifyTokenRequest request) throws ClientException {
		return invoke("VerifyTokenRequest", request, VerifyTokenResponse.class);
	}
	
	public RefreshTokenResponse refresh(RefreshTokenRequest request) throws ClientException {
		return invoke("RefreshTokenRequest", request, RefreshTokenResponse.class);
	}
	
	public RevokeTokenResponse revoke(RevokeTokenRequest request) throws ClientException {
		return invoke("IdentityProviderRevokeToken", request, RevokeTokenResponse.class);
	}
}