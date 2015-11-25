package com.nowellpoint.aws.idp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.model.idp.GetTokenRequest;
import com.nowellpoint.aws.model.idp.GetTokenResponse;
import com.nowellpoint.aws.model.idp.RefreshTokenRequest;
import com.nowellpoint.aws.model.idp.RefreshTokenResponse;
import com.nowellpoint.aws.model.idp.RevokeTokenRequest;
import com.nowellpoint.aws.model.idp.RevokeTokenResponse;
import com.nowellpoint.aws.model.idp.VerifyTokenRequest;
import com.nowellpoint.aws.model.idp.VerifyTokenResponse;

public class TestIdentityProviderClient {

	@Test
	public void testAuthenticateSuccess() {
		
		long start;
		
		IdentityProviderClient client = new IdentityProviderClient();
		
		System.out.println("get token test");
		
		start = System.currentTimeMillis();
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("STORMPATH_USERNAME"))
				.withPassword(System.getenv("STORMPATH_PASSWORD"));
		
		GetTokenResponse tokenResponse = null;
		try {
			tokenResponse = client.authenticate(tokenRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
			assertTrue(tokenResponse.getStatusCode() == 200);
			assertNotNull(tokenResponse.getToken().getAccessToken());
			assertNotNull(tokenResponse.getToken().getExpiresIn());
			assertNotNull(tokenResponse.getToken().getStormpathAccessTokenHref());
			assertNotNull(tokenResponse.getToken().getRefreshToken());
			assertNotNull(tokenResponse.getToken().getTokenType());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("verify token test");
		
		start = System.currentTimeMillis();
		
		VerifyTokenRequest verifyTokenRequest = new VerifyTokenRequest().withAccessToken(tokenResponse.getToken().getAccessToken());
		
		VerifyTokenResponse verifyTokenResponse = null;
		try {
			verifyTokenResponse = client.verify(verifyTokenRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
			assertTrue(verifyTokenResponse.getStatusCode() == 200);
			assertNotNull(verifyTokenResponse.getAuthToken());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("refresh token test");
		
		start = System.currentTimeMillis();
		
		RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest().withRefreshToken(tokenResponse.getToken().getRefreshToken());
		
		RefreshTokenResponse refreshTokenResponse = null;		
		try {
			refreshTokenResponse = client.refresh(refreshTokenRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
			assertTrue(refreshTokenResponse.getStatusCode() == 200);
			assertNotNull(refreshTokenResponse.getToken());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("revoke token test");
		
		start = System.currentTimeMillis();
		
		RevokeTokenRequest revokeTokenRequest = new RevokeTokenRequest().withAccessToken(refreshTokenResponse.getToken().getAccessToken());
		
		RevokeTokenResponse revokeTokenResponse = null;
		try {
			revokeTokenResponse = client.revoke(revokeTokenRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
			assertTrue(revokeTokenResponse.getStatusCode() == 204);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}