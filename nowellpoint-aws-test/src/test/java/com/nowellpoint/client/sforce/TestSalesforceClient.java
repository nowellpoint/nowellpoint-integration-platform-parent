package com.nowellpoint.client.sforce;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.model.Identity;

public class TestSalesforceClient {
	
	@BeforeClass
	public static void before() {
		Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
	}
	
	@Test
	public void testSalesforceClient() {
		UsernamePasswordGrantRequest request = OauthRequests.USERNAME_PASSWORD_GRANT_REQUEST
				.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setUsername(System.getenv("SALESFORCE_USERNAME"))
				.setPassword(System.getenv("SALESFORCE_PASSWORD"))
				.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
				.build();
		
		try {
			OauthAuthenticationResponse response = Authenticators.USERNAME_PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			assertNotNull(response.getToken());
			
			GetIdentityRequest getIdentityRequest = new GetIdentityRequest()
					.setAccessToken(response.getToken().getAccessToken())
					.setId(response.getToken().getId());
			
			Client client = new Client();
			
			Identity identity = client.getIdentity(getIdentityRequest);
			
			assertNotNull(identity);
				
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		}
	}
}