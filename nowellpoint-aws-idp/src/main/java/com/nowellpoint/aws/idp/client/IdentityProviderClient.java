package com.nowellpoint.aws.idp.client;

import com.nowellpoint.aws.client.AbstractClient;
import com.nowellpoint.aws.idp.model.CreateAccountRequest;
import com.nowellpoint.aws.idp.model.CreateAccountResponse;
import com.nowellpoint.aws.idp.model.GetAccountRequest;
import com.nowellpoint.aws.idp.model.GetAccountResponse;
import com.nowellpoint.aws.idp.model.GetCustomDataRequest;
import com.nowellpoint.aws.idp.model.GetCustomDataResponse;
import com.nowellpoint.aws.idp.model.GetTokenRequest;
import com.nowellpoint.aws.idp.model.GetTokenResponse;
import com.nowellpoint.aws.idp.model.RefreshTokenRequest;
import com.nowellpoint.aws.idp.model.RefreshTokenResponse;
import com.nowellpoint.aws.idp.model.RevokeTokenRequest;
import com.nowellpoint.aws.idp.model.RevokeTokenResponse;
import com.nowellpoint.aws.idp.model.SearchAccountRequest;
import com.nowellpoint.aws.idp.model.SearchAccountResponse;
import com.nowellpoint.aws.idp.model.UpdateAccountRequest;
import com.nowellpoint.aws.idp.model.UpdateAccountResponse;
import com.nowellpoint.aws.idp.model.VerifyTokenRequest;
import com.nowellpoint.aws.idp.model.VerifyTokenResponse;
import com.nowellpoint.aws.model.ClientException;

public class IdentityProviderClient extends AbstractClient {

	public IdentityProviderClient() {
		
	}
	
	public GetTokenResponse token(GetTokenRequest getTokenRequest) throws ClientException {		
		return invoke("IdentityProviderUsernamePasswordAuthentication", getTokenRequest, GetTokenResponse.class);
	}
	
	public SearchAccountResponse account(SearchAccountRequest searchAccountRequest) throws ClientException {		
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
	
	public GetCustomDataResponse account(GetCustomDataRequest request) throws ClientException {
		return invoke("IdentityProviderGetCustomData", request, GetCustomDataResponse.class);
	}
	
	public VerifyTokenResponse token(VerifyTokenRequest request) throws ClientException {
		return invoke("VerifyTokenRequest", request, VerifyTokenResponse.class);
	}
	
	public RefreshTokenResponse token(RefreshTokenRequest request) throws ClientException {
		return invoke("RefreshTokenRequest", request, RefreshTokenResponse.class);
	}
	
	public RevokeTokenResponse token(RevokeTokenRequest request) throws ClientException {
		return invoke("IdentityProviderRevokeToken", request, RevokeTokenResponse.class);
	}
}