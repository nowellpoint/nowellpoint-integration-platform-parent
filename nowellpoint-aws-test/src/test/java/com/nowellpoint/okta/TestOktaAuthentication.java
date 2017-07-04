package com.nowellpoint.okta;

import org.junit.Test;

import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.ClientBuilder;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.user.UserList;

public class TestOktaAuthentication {
	
	@Test
	public void testOktaAuthentication() {
		
		ClientBuilder builder = Clients.builder();
		
		ClientCredentials<String> credentials = new TokenClientCredentials("");
		
		Client client = builder.setClientCredentials(credentials)
				.setOrgUrl("")
				.build();
		
		UserList users = client.listUsers();
		
		users.forEach(user -> {
			System.out.println(user.getId());
			System.out.println(user.getResourceHref());
		});
		
	}
}