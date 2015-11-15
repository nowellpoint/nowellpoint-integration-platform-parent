package com.nowellpoint.aws.idp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.nowellpoint.aws.idp.model.GetTokenRequest;
import com.nowellpoint.aws.idp.model.GetTokenResponse;
import com.nowellpoint.aws.idp.model.RefreshTokenRequest;
import com.nowellpoint.aws.idp.model.RefreshTokenResponse;
import com.nowellpoint.aws.idp.model.RevokeTokenRequest;
import com.nowellpoint.aws.idp.model.RevokeTokenResponse;
import com.nowellpoint.aws.idp.model.VerifyTokenRequest;
import com.nowellpoint.aws.idp.model.VerifyTokenResponse;
import com.nowellpoint.aws.service.IdentityProviderService;

public class TestIdentityProviderService {

	@Test
	public void testAuthenticateSuccess() {
		
		long start;
		
		IdentityProviderService identityProviderService = new IdentityProviderService();
		
		System.out.println("get token test");
		
		start = System.currentTimeMillis();
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("STORMPATH_USERNAME"))
				.withPassword(System.getenv("STORMPATH_PASSWORD"));
		
		GetTokenResponse tokenResponse = null;
		try {
			tokenResponse = identityProviderService.authenticate(tokenRequest);
			
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
			verifyTokenResponse = identityProviderService.verify(verifyTokenRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
			System.out.println(verifyTokenResponse.asJson());
			
			assertTrue(tokenResponse.getStatusCode() == 200);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("refresh token test");
		
		start = System.currentTimeMillis();
		
		RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest().withRefreshToken(tokenResponse.getToken().getRefreshToken());
		
		RefreshTokenResponse refreshTokenResponse = null;		
		try {
			refreshTokenResponse = identityProviderService.refresh(refreshTokenRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
			assertTrue(refreshTokenResponse.getStatusCode() == 200);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("revoke token test");
		
		start = System.currentTimeMillis();
		
		RevokeTokenRequest revokeTokenRequest = new RevokeTokenRequest().withAccessToken(refreshTokenResponse.getToken().getAccessToken());
		
		RevokeTokenResponse revokeTokenResponse = null;
		try {
			revokeTokenResponse = identityProviderService.revoke(revokeTokenRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
			assertTrue(revokeTokenResponse.getStatusCode() == 204);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}