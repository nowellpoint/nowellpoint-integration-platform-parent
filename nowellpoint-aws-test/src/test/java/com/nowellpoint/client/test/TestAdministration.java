package com.nowellpoint.client.test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.RestResource;

public class TestAdministration {
	
	private static Token token;
	
	@BeforeClass
	public static void authenticate() {
		ClientCredentialsGrantRequest request = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setEnvironment(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")))
				.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.setApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.build();
		
		OauthAuthenticationResponse response = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		token = response.getToken();
	}
	
	@Test
	public void testPurgeCache() {
		HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path("cache")
				.execute();
		
		System.out.println(httpResponse.getStatusCode());
	}
}