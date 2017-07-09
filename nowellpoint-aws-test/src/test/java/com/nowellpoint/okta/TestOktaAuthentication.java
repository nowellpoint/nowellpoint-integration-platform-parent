package com.nowellpoint.okta;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.sdk.authc.credentials.ClientCredentials;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.ClientBuilder;
import com.okta.sdk.client.Clients;
import com.okta.sdk.clients.AuthApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.models.auth.AuthResult;
import com.okta.sdk.resource.user.UserList;



public class TestOktaAuthentication {
	
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
	
	@Test
	public void testUserAuthentication() {
		ObjectMapper mapper = new ObjectMapper();
		ApiClientConfiguration config = new ApiClientConfiguration("https://dev-309807.oktapreview.com","00TZI7_SsrpUmjCQ6uUBpZb-bBLL8UbjF08dEwZe87");
		AuthApiClient authApiClient = new AuthApiClient(config);
		try {
			AuthResult result = authApiClient.authenticate("john.d.herson@gmail.com", "d97l;lgAF", "token");
			System.out.println(result.getStatus());
			System.out.println(result.getSessionToken());
			System.out.println(result.getExpiresAt());
			User user = mapper.readValue(mapper.writeValueAsString(result.getEmbedded().get("user")), User.class);
			System.out.println(user.getId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}