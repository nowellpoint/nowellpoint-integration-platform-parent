package com.nowellpoint.client.auth;

import org.junit.Test;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.impl.OauthException;

public class TestAuthenticators {
	
	@Test
	public void testPasswordGrantAuthentication() {
		
		BasicCredentials credentials = new BasicCredentials(System.getenv("STORMPATH_USERNAME"), System.getenv("STORMPATH_PASSWORD"));
		
		try {
			NowellpointClient client = new NowellpointClient(credentials);
			
			client.logout();
			
		} catch (OauthException e) {
			System.out.println(e.getCode());
			System.out.println(e.getMessage());
		}
	}
}