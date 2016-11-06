package com.nowellpoint.client.sforce;

import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.AmazonClientException;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.sobject.Sobject;

public class TestAutheticators {
	
	private static Client client = new Client();
	
	@BeforeClass
	public static void init() {
		Properties.setSystemProperties(System.getenv("NOWELLPOINT_PROPERTY_STORE"));
	}
	
	@Test
	public void testUsernamePasswordAuthentication() {
		
		UsernamePasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setUsername(System.getenv("SALESFORCE_USERNAME"))
				.setPassword(System.getenv("SALESFORCE_PASSWORD"))
				.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
				.build();
		
		try {
			OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			assertNotNull(response.getToken());
			assertNotNull(response.getIdentity());
			assertNotNull(response.getIdentity().getAddrCity());
			
			long startTime = System.currentTimeMillis();
			
			DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
					.setAccessToken(response.getToken().getAccessToken())
					.setSobjectsUrl(response.getIdentity().getUrls().getSobjects());
			
			DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
			
			//Sat Nov 05 08:00:00 UTC 2016
			
			Calendar modifiedDate = new GregorianCalendar();
			modifiedDate.set(2016, 10, 5, 00, 00, 00);
			
			for (Sobject sobject : describeGlobalSobjectsResult.getSobjects()) {
				DescribeSobjectRequest describeSobjectRequest = new DescribeSobjectRequest()
						.withAccessToken(response.getToken().getAccessToken())
						.withSobjectsUrl(response.getIdentity().getUrls().getSobjects())
						.withSobject(sobject.getName())
						.withIfModifiedSince(modifiedDate.getTime());
				
				DescribeSobjectResult describeSobjectResult = client.describeSobject(describeSobjectRequest);
				
				if (describeSobjectResult != null) {
					System.out.println(describeSobjectResult.getName());
				}
			}
			
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