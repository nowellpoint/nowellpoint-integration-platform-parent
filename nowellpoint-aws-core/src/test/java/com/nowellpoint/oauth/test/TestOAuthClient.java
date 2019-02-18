package com.nowellpoint.oauth.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.nowellpoint.oauth.OAuthClient;
import com.nowellpoint.oauth.model.AuthenticationRequest;
import com.nowellpoint.oauth.model.OAuthProviderType;
import com.nowellpoint.oauth.model.OktaOAuthProvider;
import com.nowellpoint.oauth.model.TokenResponse;
import com.nowellpoint.oauth.model.TokenVerificationResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class TestOAuthClient {
	
	@Test
	public void testDefaultAuthenticate() {
		
		OAuthProviderType provider = OktaOAuthProvider.builder()
				.build();
		
		OAuthClient client = OAuthClient.builder()
				.provider(provider)
				.build();
		
		AuthenticationRequest request = AuthenticationRequest.builder()
				.password("mypassw0rd")
				.username("jherson@aim.com")
				.build();
		
		TokenResponse response = client.authenticate(request);
		
		assertNotNull(response);
		assertNotNull(response.getAccessToken());
		assertNotNull(response.getExpiresIn());
		assertNotNull(response.getRefreshToken());
		assertNotNull(response.getScope());
		assertNotNull(response.getTokenType());
		
		long start = System.currentTimeMillis();
		TokenVerificationResponse verificationResponse = client.verify(response.getAccessToken());
		System.out.println(System.currentTimeMillis() - start);
		
		assertNotNull(verificationResponse);
		assertNotNull(verificationResponse.getAudience());
		assertNotNull(verificationResponse.getClientId());
		
		start = System.currentTimeMillis();
		Jws<Claims> claims = client.getClaims(response.getAccessToken());
		System.out.println(System.currentTimeMillis() - start);
		
		assertNotNull(claims.getBody());
		assertNotNull(claims.getHeader());
		assertNotNull(claims.getBody().getAudience());
		
		start = System.currentTimeMillis();
		client.getClaims(response.getAccessToken());
		System.out.println(System.currentTimeMillis() - start);
		
		client.revoke(response.getAccessToken());
	}
	
	@Test
	public void testAuthenticate() {
		
		OAuthProviderType provider = OktaOAuthProvider.builder()
				.authorizationServer(System.getenv("OKTA_AUTHORIZATION_SERVER"))
				.clientId(System.getenv("OKTA_CLIENT_ID"))
				.clientSecret(System.getenv("OKTA_CLIENT_SECRET"))
				.build();
		
		OAuthClient client = OAuthClient.builder()
				.provider(provider)
				.build();
		
		AuthenticationRequest request = AuthenticationRequest.builder()
				.password("mypassw0rd")
				.username("jherson@aim.com")
				.build();
		
		TokenResponse response = client.authenticate(request);
		
		assertNotNull(response);
		assertNotNull(response.getAccessToken());
		assertNotNull(response.getExpiresIn());
		assertNotNull(response.getRefreshToken());
		assertNotNull(response.getScope());
		assertNotNull(response.getTokenType());
		
		client.revoke(response.getAccessToken());
	}
}