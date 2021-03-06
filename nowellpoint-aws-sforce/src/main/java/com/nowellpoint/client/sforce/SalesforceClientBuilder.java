package com.nowellpoint.client.sforce;

import com.nowellpoint.client.sforce.model.RefreshTokenRequest;
import com.nowellpoint.client.sforce.model.Token;

import lombok.Builder;

public class SalesforceClientBuilder {

	@Builder(builderMethodName = "defaultClient")
	public static Salesforce defaultClient(Token token) {
		return new SalesforceClient(token);
	}
	
	@Builder(builderMethodName = "newClient") 
	public static Salesforce newClient(RefreshTokenRequest refreshTokenRequest) {
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(refreshTokenRequest.getClientId())
				.setClientSecret(refreshTokenRequest.getClientSecret())
				.setRefreshToken(refreshTokenRequest.getRefreshToken())
				.build();
		
		OauthAuthenticationResponse response = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		return new SalesforceClient(response.getToken());
	}
}