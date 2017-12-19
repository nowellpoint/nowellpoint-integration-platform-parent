package com.nowellpoint.client;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.model.Connector;
import com.nowellpoint.client.model.ConnectorList;
import com.nowellpoint.client.model.ConnectorRequest;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;

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
		
		ConnectorRequest createRequest = ConnectorRequest.builder()
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
				.create(createRequest);
				
		Assert.assertTrue(createResult.isSuccess());
		Assert.assertNotNull(createResult.getTarget());
		Assert.assertNotNull(createResult.getTarget().getAuthEndpoint());
		Assert.assertNotNull(createResult.getTarget().getId());
		Assert.assertNotNull(createResult.getTarget().getName());
		Assert.assertNotNull(createResult.getTarget().getTypeName());
		Assert.assertNotNull(createResult.getTarget().getCreatedBy());
		Assert.assertNotNull(createResult.getTarget().getCreatedOn());
		Assert.assertNotNull(createResult.getTarget().getLastUpdatedBy());
		Assert.assertNotNull(createResult.getTarget().getLastUpdatedOn());
		Assert.assertNotNull(createResult.getTarget().getConnectionStatus());
		Assert.assertNotNull(createResult.getTarget().getConnectionDate());
		Assert.assertNotNull(createResult.getTarget().getMeta());
		Assert.assertNotNull(createResult.getTarget().getOwner());
		Assert.assertTrue(createResult.getTarget().getIsConnected());
		
		ConnectorRequest updateRequest = ConnectorRequest.builder()
				.name("Updated name")
				.token(token)
				.clientId(System.getenv("SALESFORCE_CLIENT_ID")) 
				.clientSecret(System.getenv("SALESFORCE_CLIENT_SECRET"))
				.username(System.getenv("SALESFORCE_USERNAME"))
				.password(System.getenv("SALESFORCE_PASSWORD").concat(System.getenv("SALESFORCE_SECURITY_TOKEN")))
				.build();
		
		UpdateResult<Connector> updateResult = NowellpointClient.defaultClient(token)
				.connector()
				.update(createResult.getTarget().getId(), updateRequest);
		
		Assert.assertTrue(updateResult.isSuccess());
		Assert.assertTrue(updateResult.getTarget().getIsConnected());
		Assert.assertTrue("Updated name".equals(updateResult.getTarget().getName()));
		
		UpdateResult<Connector> refreshResult = NowellpointClient.defaultClient(token)
				.connector()
				.refresh(createResult.getTarget().getId());
		
		Assert.assertTrue(refreshResult.isSuccess());
		Assert.assertTrue(refreshResult.getTarget().getIsConnected());
		
		UpdateResult<Connector> disconnectResult = NowellpointClient.defaultClient(token)
				.connector()
				.disconnect(createResult.getTarget().getId());
		
		Assert.assertTrue(disconnectResult.isSuccess());
		Assert.assertFalse(disconnectResult.getTarget().getIsConnected());
		
		ConnectorList connectorList = NowellpointClient.defaultClient(token)
				.connector()
				.getConnectors();
		
		Assert.assertTrue(connectorList.getSize() > 0);
		
		DeleteResult deleteResult = NowellpointClient.defaultClient(token)
				.connector()
				.delete(createResult.getTarget().getId());
		
		Assert.assertTrue(deleteResult.isSuccess());
	}
	
	@AfterClass
	public static void logout() {
		token.delete();
	}
}