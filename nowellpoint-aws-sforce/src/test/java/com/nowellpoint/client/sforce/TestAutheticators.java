package com.nowellpoint.client.sforce;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.AmazonClientException;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.DescribeResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;

public class TestAutheticators {
	
	private static Salesforce client = SalesforceClientBuilder.builder().build().getClient();
	
	@BeforeClass
	public static void init() {
		
	}
	
	@Test
	public void testUsernamePasswordAuthentication() {
		
		UsernamePasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(""))
				.setClientSecret(System.getProperty(""))
				.setUsername(System.getenv("SALESFORCE_USERNAME"))
				.setPassword(System.getenv("SALESFORCE_PASSWORD"))
				.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
				.build();
		
		try {
			OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			Token token = response.getToken();
			
			assertNotNull(token);
			assertNotNull(token.getAccessToken());			
			assertNotNull(token.getId());
			assertNotNull(token.getInstanceUrl());
			assertNotNull(token.getIssuedAt());
			assertNotNull(token.getSignature());
			assertNotNull(token.getTokenType());
			
			Identity identity = client.getIdentity(token);
			
			assertNotNull(identity);
			assertNotNull(identity.getActive());
			assertNotNull(identity.getCity());
			assertNotNull(identity.getCountry());
			assertNotNull(identity.getState());
			assertNotNull(identity.getStreet());
			assertNotNull(identity.getPostalCode());
			assertNotNull(identity.getAssertedUser());
			assertNotNull(identity.getDisplayName());
			assertNotNull(identity.getEmail());
			assertNotNull(identity.getFirstName());
			assertNotNull(identity.getLastName());
			assertNotNull(identity.getId());
			assertNotNull(identity.getLanguage());
			assertNotNull(identity.getLocale());
			
			long startTime = System.currentTimeMillis();
			
			DescribeGlobalResult describeGlobalSobjectsResult = client.describeGlobal(response.getToken());
			
			assertNotNull(describeGlobalSobjectsResult.getSObjects());
			
			DescribeResult describeSobjectResult = client.describeSObject(response.getToken(), "Account");
			
			assertNotNull(describeSobjectResult.getName());
			
			Theme theme = client.getTheme(response.getToken());

			assertNotNull(theme.getThemeItems());
			
			Long count = client.count(response.getToken(), "Select count() from Account");
			
			assertNotNull(count);
			
			System.out.println(count);
			
			System.out.println("Process duration (ms): " + (System.currentTimeMillis() - startTime));
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		} catch (ClientException e) {
			System.out.println(e.getErrorDescription());
			System.out.println(e.getError());
			System.out.println(e.getStatusCode());
		} catch (AmazonClientException e) {
	    	System.out.println(e.getMessage());
	    }
	}
}