package com.nowellpoint.client.sforce.test;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.SalesforceClientException;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.Account;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.DescribeResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Limits;
import com.nowellpoint.client.sforce.model.Resources;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.UserLicense;
import com.nowellpoint.util.SecretsManager;

public class TestSalesforceClient {
	
	private static Salesforce client;
	
	@BeforeClass
	public static void init() {
		
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
			
			client = SalesforceClientBuilder.defaultClient(token);
			
			assertNotNull(token);
			assertNotNull(token.getAccessToken());			
			assertNotNull(token.getId());
			assertNotNull(token.getInstanceUrl());
			assertNotNull(token.getIssuedAt());
			assertNotNull(token.getSignature());
			assertNotNull(token.getTokenType());
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getMessage());
			System.out.println(e.getErrorDescription());
		} 	
		
	}
	
	@Test
	public void testReflection() {
		Account account = client.findById(Account.class, "0013000001Fc0b0AAB");
		
		assertNotNull(account.getId());
		assertNotNull(account.getName());
		assertNotNull(account.getBillingCity());
		assertNotNull(account.getOwnership());
		assertNotNull(account.getNumberOfEmployees());
		assertNotNull(account.getOwner().getName());
		assertNotNull(account.getCreatedDate());
		assertNotNull(account.getAttributes().getType());
		assertNotNull(account.getAttributes().getUrl());

	}
	
	@Test
	public void testUsernamePasswordAuthentication() {
		try {
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
			
			Limits limits = client.getLimits();
			
			assertNotNull(limits);
			
			UserLicense[] userLicenses = client.getUserLicenses();
			
			assertNotNull(userLicenses);
			
			Resources resources = client.getResources();
			
			assertNotNull(resources);
			
			System.out.println(resources.getIdentity());
			
			System.out.println("Process duration (ms): " + (System.currentTimeMillis() - startTime));
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getMessage());
			System.out.println(e.getErrorDescription());
		} catch (SalesforceClientException e) {
			System.out.println(e.getErrorCode());
			System.out.println(e.getMessage());
		} 
	}
}