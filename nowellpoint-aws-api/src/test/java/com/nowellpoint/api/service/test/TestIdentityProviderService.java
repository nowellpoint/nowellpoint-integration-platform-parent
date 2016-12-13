package com.nowellpoint.api.service.test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.aws.model.admin.Properties;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.OAuthClientCredentialsGrantRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRequests;

public class TestIdentityProviderService {
	
	private static Client client;
	private static ApiKey apiKey;
	private static Application application;
	
	@BeforeClass
	public static void beforeClass() {
		Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
		
		System.out.println(System.getProperty(Properties.STORMPATH_API_KEY_ID));
		System.out.println(System.getProperty(Properties.STORMPATH_API_KEY_SECRET));
		
		apiKey = ApiKeys.builder()
				.setId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.setSecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.build();
		
		client = Clients.builder()
				.setApiKey(apiKey)
				.build();
		
		application = client.getResource(System.getProperty(Properties.STORMPATH_API_ENDPOINT).concat("/applications/")
				.concat(System.getProperty(Properties.STORMPATH_APPLICATION_ID)), Application.class);
		
		System.out.println(application.getName());
	}
	
	@Test
	public void testAuthentication() {
		OAuthClientCredentialsGrantRequestAuthentication request = OAuthRequests.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST
				.builder()
				.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.setApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.build();
		
		OAuthGrantRequestAuthenticationResult authenticationResult = Authenticators.OAUTH_CLIENT_CREDENTIALS_GRANT_REQUEST_AUTHENTICATOR
				.forApplication(application)
				.authenticate(request);
		
		System.out.println(authenticationResult.getAccessToken().getAccount().getFullName());
		System.out.println(authenticationResult.getAccessToken().getJwt());
	}
}