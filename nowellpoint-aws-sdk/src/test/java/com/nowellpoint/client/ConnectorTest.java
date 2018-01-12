package com.nowellpoint.client;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;

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
				.name("Test Salesforce Connector")
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
		Assert.assertNotNull(createResult.getTarget().getIsSandbox());
		Assert.assertNotNull(createResult.getTarget().getTypeName());
		Assert.assertNotNull(createResult.getTarget().getCreatedBy());
		Assert.assertNotNull(createResult.getTarget().getCreatedOn());
		Assert.assertNotNull(createResult.getTarget().getLastUpdatedBy());
		Assert.assertNotNull(createResult.getTarget().getLastUpdatedOn());
		Assert.assertNotNull(createResult.getTarget().getStatus());
		Assert.assertNotNull(createResult.getTarget().getConnectedOn());
		Assert.assertNotNull(createResult.getTarget().getMeta());
		Assert.assertNotNull(createResult.getTarget().getOwner());
		Assert.assertNotNull(createResult.getTarget().getConnectedAs());
		Assert.assertTrue(createResult.getTarget().getIsConnected());
		
		ConnectorRequest updateRequest = ConnectorRequest.builder()
				.name("Updated Salesforce Connector")
				.token(token)
				.build();
		
		UpdateResult<Connector> updateResult = NowellpointClient.defaultClient(token)
				.connector()
				.update(createResult.getTarget().getId(), updateRequest);
		
		Assert.assertTrue(updateResult.isSuccess());
		Assert.assertNotNull(updateResult.getTarget().getStatus());
		Assert.assertTrue(updateResult.getTarget().getIsConnected());
		Assert.assertTrue("Updated Salesforce Connector".equals(updateResult.getTarget().getName()));
		
		UpdateResult<Connector> refreshResult = NowellpointClient.defaultClient(token)
				.connector()
				.refresh(createResult.getTarget().getId());
		
		System.out.println(refreshResult.getErrorMessage());
		
		Assert.assertTrue(refreshResult.isSuccess());
		Assert.assertNotNull(refreshResult.getTarget().getStatus());
		Assert.assertTrue(refreshResult.getTarget().getIsConnected());
		
		UpdateResult<Connector> disconnectResult = NowellpointClient.defaultClient(token)
				.connector()
				.disconnect(createResult.getTarget().getId());
		
		Assert.assertTrue(disconnectResult.isSuccess());
		Assert.assertNotNull(disconnectResult.getTarget().getStatus());
		Assert.assertFalse(disconnectResult.getTarget().getIsConnected());
		
		ConnectorRequest updateRequest3 = ConnectorRequest.builder()
				.name("Updated Salesforce Connector")
				.token(token)
				.clientId(System.getenv("SALESFORCE_CLIENT_ID")) 
				.clientSecret(System.getenv("SALESFORCE_CLIENT_SECRET"))
				.username(System.getenv("SALESFORCE_USERNAME"))
				.password(System.getenv("SALESFORCE_PASSWORD").concat(System.getenv("SALESFORCE_SECURITY_TOKEN")))
				.build();
		
		UpdateResult<Connector> updateResult3 = NowellpointClient.defaultClient(token)
				.connector()
				.connect(createResult.getTarget().getId(), updateRequest3);
		
		Assert.assertTrue(updateResult3.isSuccess());
		
		ConnectorRequest updateRequest2 = ConnectorRequest.builder()
				.name("Updated Salesforce Connector")
				.token(token)
				.clientId(System.getenv("SALESFORCE_CLIENT_ID")) 
				.clientSecret(System.getenv("SALESFORCE_CLIENT_SECRET"))
				.username(System.getenv("SALESFORCE_USERNAME"))
				.password(System.getenv("SALESFORCE_PASSWORD").concat(System.getenv("SALESFORCE_SECURITY_TOKEN")))
				.build();
		
		UpdateResult<Connector> updateResult2 = NowellpointClient.defaultClient(token)
				.connector()
				.update(createResult.getTarget().getId(), updateRequest2);
		
		Assert.assertTrue(updateResult2.isSuccess());
		Assert.assertNotNull(updateResult2.getTarget().getStatus());
		Assert.assertTrue(updateResult2.getTarget().getIsConnected());
		
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