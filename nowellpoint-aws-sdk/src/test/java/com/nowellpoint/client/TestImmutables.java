package com.nowellpoint.client;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Token;

public class TestImmutables {
	
	@Test
	public void testClient() {
		ClientCredentialsGrantRequest request = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.setApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.setEnvironment(Environment.SANDBOX)
				.build();
		
		OauthAuthenticationResponse response = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		Token token = response.getToken();
		
		NowellpointSDK sdk = NowellpointSDK.builder()
				.token(token)
				.build();
		
		Identity identity = sdk.identity().get(token.getId());
		
		System.out.println(identity.getName());
		System.out.println(identity.getSubscription().getPlanName());
		
		//Plan plan = sdk.plan().get(identity.get)
		
		assertNotNull(token.getAccessToken());
		assertNotNull(token.getEnvironmentUrl());
		assertNotNull(token.getExpiresIn());
		assertNotNull(token.getId());
		assertNotNull(token.getTokenType());
		
		token.logout();
		
		assertNull(token.getAccessToken());
		assertNull(token.getEnvironmentUrl());
		assertNull(token.getExpiresIn());
		assertNull(token.getId());
		assertNull(token.getTokenType());
	}
}