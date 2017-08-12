package com.nowellpoint.client;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Registration;
import com.nowellpoint.client.model.SignUpRequest;
import com.nowellpoint.client.model.Token;

public class TestImmutables {
	
	@Test
	public void testClient() {
		ClientCredentialsGrantRequest request = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.setApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.setEnvironment(Environment.SANDBOX)
				.build();
		
		PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setEnvironment(Environment.SANDBOX)
				.setPassword(System.getenv("NOWELLPOINT_PASSWORD"))
				.setUsername(System.getenv("NOWELLPOINT_USERNAME"))
				.build();
		
		OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
				.authenticate(passwordGrantRequest);
		
		Token token = response.getToken();
		
		assertNotNull(token.getAccessToken());
		assertNotNull(token.getEnvironmentUrl());
		assertNotNull(token.getExpiresIn());
		assertNotNull(token.getId());
		assertNotNull(token.getTokenType());
		
		Identity identity = NowellpointClient.defaultClient(token)
				.identity()
				.get(token.getId());
		
		assertNotNull(identity.getOrganization());
		assertNotNull(identity.getOrganization().getSubscription());
		assertNotNull(identity.getOrganization().getSubscription().getPlanId());
		assertNotNull(identity.getOrganization().getSubscription().getPlanName());
		
		token.delete();
		
		assertNull(token.getAccessToken());
		assertNull(token.getEnvironmentUrl());
		assertNull(token.getExpiresIn());
		assertNull(token.getId());
		assertNull(token.getTokenType());
	}
	
	@Test
	public void testCreateFreePlan() {
		
		SignUpRequest signUpRequest = SignUpRequest.builder()
				.countryCode("US")
				.email("jherson@aim.com")
				.firstName("John")
				.lastName("Herson")
				.planId("57fa74a601936217bb99643c")
				.build();
		
		CreateResult<Registration> registration = NowellpointClient.defaultClient(Environment.SANDBOX)
				.registration()
				.signUp(signUpRequest);
		
		
		
	}
}