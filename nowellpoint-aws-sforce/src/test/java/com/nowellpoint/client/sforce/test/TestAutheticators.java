package com.nowellpoint.client.sforce.test;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientException;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.DescribeResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.util.SecretsManager;

public class TestAutheticators {
	
	@BeforeClass
	public static void init() {
		
	}
	
	@Test
	public void testUsernamePasswordAuthentication() {
		
		UsernamePasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
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
			
			Salesforce client = SalesforceClientBuilder.defaultClient(token);
			
			Identity identity = client.getIdentity();
			
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
			
			DescribeGlobalResult describeGlobalSobjectsResult = client.describeGlobal();
			
			assertNotNull(describeGlobalSobjectsResult.getSObjects());
			
			DescribeResult describeSobjectResult = client.describeSObject("Account");
			
			assertNotNull(describeSobjectResult.getName());
			
			Theme theme = client.getTheme();

			assertNotNull(theme.getThemeItems());
			
			Long count = client.count("Select count() from Account");
			
			assertNotNull(count);
			
			System.out.println(count);
			
			System.out.println("Process duration (ms): " + (System.currentTimeMillis() - startTime));
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getError());
			System.out.println(e.getErrorDescription());
		} catch (SalesforceClientException e) {
			System.out.println(e.getMessage());
			System.out.println(e.getErrorCode());
		} 
	}
}