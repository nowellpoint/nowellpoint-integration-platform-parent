package com.nowellpoint.client.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.model.Token;

public class TestAuthenticate {
	
	@Test
	public void testClientCredentialsAuthentication() {
		
		ClientCredentialsGrantRequest request = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setEnvironment(Environment.SANDBOX)
				.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.setApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.build();
		
		OauthAuthenticationResponse response = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		Token token = response.getToken();
		
		assertNotNull(token);
		assertNotNull(token.getExpiresIn());
		assertNotNull(token.getTokenType());
		assertNotNull(token.getAccessToken());
		
		System.out.println(token.getAccessToken());
		
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setToken(token)
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
	}
	
	@Test
	public void testUsernamePasswordAuthentication() {
		
		PasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setEnvironment(Environment.SANDBOX)
				.setUsername(System.getenv("STORMPATH_USERNAME"))
				.setPassword(System.getenv("STORMPATH_PASSWORD"))
				.build();
		
		OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		Token token = response.getToken();
		
		assertNotNull(token);
		assertNotNull(token.getExpiresIn());
		assertNotNull(token.getTokenType());
		assertNotNull(token.getRefreshToken());
		assertNotNull(token.getAccessToken());
		
		System.out.println(token.getAccessToken());
		
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setToken(token)
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);	
	}
	
	@Test(expected=com.nowellpoint.client.auth.impl.OauthException.class)
	public void testUsernamePasswordAuthenticationException() {
		
		PasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setEnvironment(Environment.SANDBOX)
				.setUsername(System.getenv("STORMPATH_USERNAME"))
				.setPassword("STORMPATH_PASSWORD")
				.build();
		
		Authenticators.PASSWORD_GRANT_AUTHENTICATOR.authenticate(request);
	}
	
	@Test(expected=com.nowellpoint.client.auth.impl.OauthException.class)
	public void testClientCredentialsAuthenticationException() {
		
		ClientCredentialsGrantRequest request = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setEnvironment(Environment.SANDBOX)
				.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.setApiKeySecret("STORMPATH_API_KEY_SECRET")
				.build();
		
		Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR.authenticate(request);

	}
}