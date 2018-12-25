package com.nowellpoint.client.sforce;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.AmazonClientException;
import com.nowellpoint.client.sforce.model.DescribeGlobalResult;
import com.nowellpoint.client.sforce.model.DescribeResult;
import com.nowellpoint.client.sforce.model.Theme;

public class TestAutheticators {
	
	private static Client client = new Client();
	
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
			
			System.out.println(response.getToken().getAccessToken());			
			System.out.println(response.getToken().getId());
			System.out.println(response.getToken().getInstanceUrl());
			System.out.println(response.getToken().getIssuedAt());
			System.out.println(response.getToken().getSignature());
			System.out.println(response.getToken().getTokenType());
			
			assertNotNull(response.getToken());
			assertNotNull(response.getIdentity());
			assertNotNull(response.getIdentity().getAddrCity());
			
			long startTime = System.currentTimeMillis();
			
			DescribeGlobalResult describeGlobalSobjectsResult = client.describeGlobal(response.getToken());
			
			assertNotNull(describeGlobalSobjectsResult.getSObjects());
			
			DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
					.withAccessToken(response.getToken().getAccessToken())
					.withSobjectsUrl(response.getIdentity().getUrls().getSObjects())
					.withSobject(describeGlobalSobjectsResult.getSObjects().get(0).getName());
			
			DescribeResult describeSobjectResult = client.describeSobject(describeSobjectRequest);
			
			assertNotNull(describeSobjectResult.getName());

			ThemeRequest themeRequest = new ThemeRequest()
					.withAccessToken(response.getToken().getAccessToken())
					.withRestEndpoint(response.getIdentity().getUrls().getRest());
			
			Theme theme = client.getTheme(themeRequest);

			assertNotNull(theme.getThemeItems());
			
			CountRequest countRequest = new CountRequest()
					.withAccessToken(response.getToken().getAccessToken())
					.withQueryUrl(response.getIdentity().getUrls().getQuery())
					.withSobject("Vote");
			
			Long count = client.getCount(countRequest);
			
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