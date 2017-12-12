package com.nowellpoint.client;

import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.model.Connector;
import com.nowellpoint.client.model.ConnectorRequest;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.Token;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConnectorTest {
	
	private static Token token;
	
	@BeforeClass
	public static void authenticate() {
		PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setEnvironment(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")))
				.setUsername("jherson@aim.com")
				.setPassword("mypassw0rd")
				.build();

		OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
				.authenticate(passwordGrantRequest);

		token = oauthAuthenticationResponse.getToken();
		
		System.out.println(token.getAccessToken());
	}
	
	@Test
	public void testCreateConnector() {
		
		ConnectorRequest request = ConnectorRequest.builder()
				.name("test Salesforce Connector")
				.token(token)
				.type("SALESFORCE_PRODUCTION")
				.clientId(System.getenv("SALESFORCE_CLIENT_ID")) 
				.clientSecret(System.getenv("SALESFORCE_CLIENT_SECRET"))
				.username(System.getenv("SALESFORCE_USERNAME"))
				.password(System.getenv("SALESFORCE_PASSWORD").concat(System.getenv("SALESFORCE_SECURITY_TOKEN")))
				.build();
		
		CreateResult<Connector> createResult = NowellpointClient.defaultClient(token)
				.connector()
				.create(request);
		
		Assert.assertTrue(createResult.isSuccess());
		Assert.assertNotNull(createResult.getTarget());
		Assert.assertNotNull(createResult.getTarget().getAuthEndpoint());
		Assert.assertNotNull(createResult.getTarget().getId());
		Assert.assertNotNull(createResult.getTarget().getName());
		Assert.assertNotNull(createResult.getTarget().getType());
		Assert.assertNotNull(createResult.getTarget().getTypeName());
		Assert.assertNotNull(createResult.getTarget().getCreatedBy());
		Assert.assertNotNull(createResult.getTarget().getCreatedOn());
		Assert.assertNotNull(createResult.getTarget().getLastUpdatedBy());
		Assert.assertNotNull(createResult.getTarget().getLastUpdatedOn());
		Assert.assertNotNull(createResult.getTarget().getConnectionStatus());
		Assert.assertNotNull(createResult.getTarget().getConnectionDate());
		Assert.assertNotNull(createResult.getTarget().getMeta());
		Assert.assertNotNull(createResult.getTarget().getOwner());
	}
	
	@AfterClass
	public static void logout() {
		token.delete();
	}
}