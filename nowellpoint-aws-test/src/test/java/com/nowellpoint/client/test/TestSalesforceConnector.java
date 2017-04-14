package com.nowellpoint.client.test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.NowellpointClientOrig;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.model.SObject;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorList;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;

public class TestSalesforceConnector {
	
	private static Token token;
	
	@BeforeClass
	public static void init() {
		ClientCredentialsGrantRequest request = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setApiKeyId(System.getenv("STORMPATH_API_KEY_ID"))
				.setApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"))
				.setEnvironment(Environment.SANDBOX)
				.build();
		
		OauthAuthenticationResponse response = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		token = response.getToken();
	}
	
	@Test
	public void testSalesforceConnectorTest() {
		
		SalesforceConnectorList salesforceConnectors = new NowellpointClientOrig(token)
				.salesforceConnector()
				.getSalesforceConnectors();
		
		SalesforceConnector salesforceConnector = salesforceConnectors.getItems().get(0);
		
		UpdateResult<SalesforceConnector> updateResult = new NowellpointClientOrig(token)
				.salesforceConnector()
				.test(salesforceConnector.getId());
		
		System.out.println(updateResult.isSuccess());
		System.out.println(updateResult.getTarget().getStatus());
	}
	
	@Test
	public void testSalesforceConnectorBuild() {
		
		SalesforceConnectorList salesforceConnectors = new NowellpointClientOrig(token)
				.salesforceConnector()
				.getSalesforceConnectors();
		
		SalesforceConnector salesforceConnector = salesforceConnectors.getItems().get(0);
		
		UpdateResult<SalesforceConnector> updateResult = new NowellpointClientOrig(token)
				.salesforceConnector()
				.build(salesforceConnector.getId());
		
		System.out.println(updateResult.isSuccess());
		System.out.println(updateResult.getTarget().getStatus());
		
		SObject sobject = new NowellpointClientOrig(token)
				.salesforceConnector()
				.sobject()
				.get(salesforceConnector.getId(), salesforceConnector.getSobjects().get(0).getName());
		
		System.out.println(sobject.getId());
	}
}