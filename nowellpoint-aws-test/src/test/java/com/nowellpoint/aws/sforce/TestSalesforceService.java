package com.nowellpoint.aws.sforce;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.nowellpoint.aws.service.SalesforceService;
import com.nowellpoint.aws.sforce.model.GetIdentityRequest;
import com.nowellpoint.aws.sforce.model.GetIdentityResponse;
import com.nowellpoint.aws.sforce.model.GetTokenRequest;
import com.nowellpoint.aws.sforce.model.GetTokenResponse;
import com.nowellpoint.aws.sforce.model.RevokeTokenRequest;
import com.nowellpoint.aws.sforce.model.RevokeTokenResponse;

public class TestSalesforceService {
	
	@Test
	public void testAuthenticateSucess() {
		
		long start;
		
		SalesforceService salesforceService = new SalesforceService();
		
		start = System.currentTimeMillis();
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("SALESFORCE_USERNAME"))
				.withPassword(System.getenv("SALESFORCE_PASSWORD"))
				.withSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"));
		
		GetTokenResponse tokenResponse = null;
		try {
			tokenResponse = salesforceService.authenticate(tokenRequest);
			
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
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		start = System.currentTimeMillis();
		
		GetIdentityRequest identityRequest = new GetIdentityRequest().withAccessToken(tokenResponse.getToken().getAccessToken())
				.withId(tokenResponse.getToken().getId());
		
		GetIdentityResponse identityResponse = null;
		try {
			identityResponse = salesforceService.getIdentity(identityRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));		
			
			assertTrue(identityResponse.getStatusCode() == 200);
			assertNotNull(identityResponse.getIdentity());
			assertNotNull(identityResponse.getIdentity().getActive());
			assertNotNull(identityResponse.getIdentity().getAddrCity());
			assertNotNull(identityResponse.getIdentity().getAddrCountry());
			assertNotNull(identityResponse.getIdentity().getAddrState());
			assertNotNull(identityResponse.getIdentity().getAddrStreet());
			assertNotNull(identityResponse.getIdentity().getAddrZip());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		start = System.currentTimeMillis();
		
		RevokeTokenRequest revokeTokenRequest = new RevokeTokenRequest().withAccessToken(tokenResponse.getToken().getAccessToken());
		
		RevokeTokenResponse revokeTokenResponse = null;
		try {
			revokeTokenResponse = salesforceService.revoke(revokeTokenRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
			assertTrue(revokeTokenResponse.getStatusCode() == 200);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}