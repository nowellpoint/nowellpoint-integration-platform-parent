package com.nowellpoint.client.sforce;

import com.nowellpoint.client.sforce.model.RefreshTokenRequest;
import com.nowellpoint.client.sforce.model.Token;

public class Authenticator {
	
	private Authenticator() {}

	public static Token refreshToken(RefreshTokenRequest refreshTokenRequest) {
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(refreshTokenRequest.getClientId())
				.setClientSecret(refreshTokenRequest.getClientSecret())
				.setRefreshToken(refreshTokenRequest.getRefreshToken())
				.build();
		
		OauthAuthenticationResponse response = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		return response.getToken();
    }
}