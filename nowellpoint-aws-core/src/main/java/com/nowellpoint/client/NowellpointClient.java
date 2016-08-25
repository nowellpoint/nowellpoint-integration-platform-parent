package com.nowellpoint.client;

import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.BasicCredentials;
import com.nowellpoint.client.auth.Credentials;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.auth.RevokeTokenRequest;

public class NowellpointClient {
	
	private static Token token;
	
	public NowellpointClient(Credentials credentials) {
		if (credentials instanceof BasicCredentials) {
			PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
					.setUsername(((BasicCredentials) credentials).getUsername())
					.setPassword(((BasicCredentials) credentials).getPassword())
					.build();
		
			OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
						.authenticate(passwordGrantRequest);
			
			token = oauthAuthenticationResponse.getToken();
		}
	}
	
	public void logout() {
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setAccessToken(token.getAccessToken())
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
	}
}