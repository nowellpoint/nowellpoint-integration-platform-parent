package com.nowellpoint.client.auth;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.nowellpoint.client.auth.impl.OauthException;

public class TestAuthenticators {
	
	@Test
	public void testPasswordGrantAuthentication() {
		PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setUsername(System.getenv("STORMPATH_USERNAME"))
				.setPassword(System.getenv("STORMPATH_PASSWORD"))
				.build();
		
		try {
			OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(passwordGrantRequest);
			
			assertNotNull(oauthAuthenticationResponse.getToken());
			assertNotNull(oauthAuthenticationResponse.getToken().getAccessToken());
			assertNotNull(oauthAuthenticationResponse.getToken().getExpiresIn());
			assertNotNull(oauthAuthenticationResponse.getToken().getRefreshToken());
			assertNotNull(oauthAuthenticationResponse.getToken().getStormpathAccessTokenHref());
			assertNotNull(oauthAuthenticationResponse.getToken().getTokenType());
			
		} catch (OauthException e) {
			System.out.println(e.getCode());
			System.out.println(e.getMessage());
		}
	}
}