package com.nowellpoint.aws.sforce;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.admin.PropertyStore;
import com.nowellpoint.aws.model.sforce.CreateSObjectRequest;
import com.nowellpoint.aws.model.sforce.CreateSObjectResponse;
import com.nowellpoint.aws.model.sforce.GetIdentityRequest;
import com.nowellpoint.aws.model.sforce.GetIdentityResponse;
import com.nowellpoint.aws.model.sforce.GetTokenRequest;
import com.nowellpoint.aws.model.sforce.GetTokenResponse;
import com.nowellpoint.aws.model.sforce.RevokeTokenRequest;
import com.nowellpoint.aws.model.sforce.RevokeTokenResponse;

public class TestSalesforceClient {
	
	@BeforeClass
	public static void before() {
		Properties.setSystemProperties(PropertyStore.PRODUCTION);
	}
	
	@Test
	public void testAuthenticateSucess() {
		
		long start;
		
		SalesforceClient client = new SalesforceClient();
		
		start = System.currentTimeMillis();
		
		GetTokenRequest tokenRequest = new GetTokenRequest()
				.withTokenUri(System.getProperty(Properties.SALESFORCE_TOKEN_URI))
				.withClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.withClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.withUsername(System.getenv("SALESFORCE_USERNAME"))
				.withPassword(System.getenv("SALESFORCE_PASSWORD"))
				.withSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"));
		
		GetTokenResponse tokenResponse = client.authenticate(tokenRequest);
			
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
		
		start = System.currentTimeMillis();
		
		GetIdentityRequest identityRequest = new GetIdentityRequest()
				.withAccessToken(tokenResponse.getToken().getAccessToken())
				.withId(tokenResponse.getToken().getId());
		
		GetIdentityResponse identityResponse = client.getIdentity(identityRequest);
			
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));		
			
		assertTrue(identityResponse.getStatusCode() == 200);
		assertNotNull(identityResponse.getIdentity());
		assertNotNull(identityResponse.getIdentity().getActive());
		assertNotNull(identityResponse.getIdentity().getAddrCity());
		assertNotNull(identityResponse.getIdentity().getAddrCountry());
		assertNotNull(identityResponse.getIdentity().getAddrState());
		assertNotNull(identityResponse.getIdentity().getAddrStreet());
		assertNotNull(identityResponse.getIdentity().getAddrZip());
		
		CreateSObjectRequest createSObjectRequest = new CreateSObjectRequest()
				.withAccessToken(tokenResponse.getToken().getAccessToken())
				.withInstanceUrl(tokenResponse.getToken().getInstanceUrl())
				.withSObject("{\"firstName\":\"John\",\"lastName\":\"Herson\",\"phone\":\"6787730798\",\"description\":\"lead\",\"company\":\"Nowellpoint\",\"email\":\"john.d.herson@gmail.com\",\"status\":\"New\"}")
				.withType("Lead");
		
		CreateSObjectResponse createSObjectResponse = client.createSObject(createSObjectRequest);
		
		assertTrue(createSObjectResponse.getStatusCode() == 201);
		assertNotNull(createSObjectResponse.getId());
		
		System.out.println("status code: " + createSObjectResponse.getStatusCode());
		System.out.println(createSObjectResponse.getId());
		
		start = System.currentTimeMillis();
		
		RevokeTokenRequest revokeTokenRequest = new RevokeTokenRequest()
				.withRevokeTokenUri(System.getProperty(Properties.SALESFORCE_REVOKE_URI))
				.withAccessToken(tokenResponse.getToken().getAccessToken());
		
		RevokeTokenResponse revokeTokenResponse = client.revoke(revokeTokenRequest);
			
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
		assertTrue(revokeTokenResponse.getStatusCode() == 200);
		
	}
}