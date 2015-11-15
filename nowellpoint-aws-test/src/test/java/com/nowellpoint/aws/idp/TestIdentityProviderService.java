package com.nowellpoint.aws.idp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.nowellpoint.aws.idp.model.GetTokenRequest;
import com.nowellpoint.aws.idp.model.GetTokenResponse;
import com.nowellpoint.aws.service.IdentityProviderService;

public class TestIdentityProviderService {

	@Test
	public void testAuthenticateSuccess() {
		
		long start;
		
		IdentityProviderService identityProviderService = new IdentityProviderService();
		
		start = System.currentTimeMillis();
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("STORMPATH_USERNAME"))
				.withPassword(System.getenv("STORMPATH_PASSWORD"));
		
		try {
			GetTokenResponse tokenResponse = identityProviderService.authenticate(tokenRequest);
			
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
	}
}