package com.nowellpoint.aws.sforce;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.nowellpoint.aws.service.SforceService;
import com.nowellpoint.aws.sforce.model.GetIdentityRequest;
import com.nowellpoint.aws.sforce.model.GetIdentityResponse;
import com.nowellpoint.aws.sforce.model.GetTokenRequest;
import com.nowellpoint.aws.sforce.model.GetTokenResponse;

public class TestSforceService {
	
	@Test
	public void testAuthenticateSucess() {
		
		long start = System.currentTimeMillis();
		
		SforceService sforceService = new SforceService();
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("SALESFORCE_USERNAME"))
				.withPassword(System.getenv("SALESFORCE_PASSWORD"))
				.withSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"));
		
		try {
			GetTokenResponse tokenResponse = sforceService.authenticate(tokenRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));		
			
			assertTrue(tokenResponse.getStatusCode() == 200);
			assertNotNull(tokenResponse.getToken());
			assertNotNull(tokenResponse.getToken().getAccessToken());
			assertNotNull(tokenResponse.getToken().getId());
			assertNotNull(tokenResponse.getToken().getInstanceUrl());
			assertNotNull(tokenResponse.getToken().getIssuedAt());
			assertNotNull(tokenResponse.getToken().getSignature());
			assertNotNull(tokenResponse.getToken().getTokenType());
			assertNull(tokenResponse.getToken().getRefreshToken());
			assertNull(tokenResponse.getToken().getScope());
			
			GetIdentityRequest identityRequest = new GetIdentityRequest().withAccessToken(tokenResponse.getToken().getAccessToken())
					.withId(tokenResponse.getToken().getId());
			
			GetIdentityResponse identityResponse = sforceService.getIdentity(identityRequest);
			
			System.out.println(identityResponse.getAsJson());
			
			//System.out.println("Display Name: " + identityResponse.getIdentity().getDisplayName());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}