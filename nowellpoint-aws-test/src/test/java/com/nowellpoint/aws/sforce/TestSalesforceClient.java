package com.nowellpoint.aws.sforce;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.model.sforce.GetIdentityRequest;
import com.nowellpoint.aws.model.sforce.GetIdentityResponse;
import com.nowellpoint.aws.model.sforce.GetTokenRequest;
import com.nowellpoint.aws.model.sforce.GetTokenResponse;
import com.nowellpoint.aws.model.sforce.RevokeTokenRequest;
import com.nowellpoint.aws.model.sforce.RevokeTokenResponse;

public class TestSalesforceClient {
	
	@Test
	public void testAuthenticateSucess() {
		
		long start;
		
		SalesforceClient client = new SalesforceClient();
		
		start = System.currentTimeMillis();
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("SALESFORCE_USERNAME"))
				.withPassword(System.getenv("SALESFORCE_PASSWORD"))
				.withSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"));
		
		GetTokenResponse tokenResponse = null;
		try {
			tokenResponse = client.authenticate(tokenRequest);
			
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
			identityResponse = client.getIdentity(identityRequest);
			
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
			revokeTokenResponse = client.revoke(revokeTokenRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
			assertTrue(revokeTokenResponse.getStatusCode() == 200);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}