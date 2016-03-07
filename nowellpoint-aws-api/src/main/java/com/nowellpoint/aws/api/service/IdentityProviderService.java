package com.nowellpoint.aws.api.service;

import com.nowellpoint.aws.model.admin.Properties;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;

public class IdentityProviderService {
	
	private static final Application application;
	
	static {
		ApiKey apiKey = ApiKeys.builder()
				.setId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.setSecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.build();
		
		Client client = Clients.builder()
				.setApiKey(apiKey)
				.build();
		
		application = client.getResource(System.getProperty(Properties.STORMPATH_APPLICATION_ID), Application.class);
	}

	public void authenticate(String username, String password) {
		AuthenticationRequest<?, ?> authenticationRequest = UsernamePasswordRequest.builder()
	            .setUsernameOrEmail(username)
	            .setPassword(password)
	            .build();
		
		AuthenticationResult result = application.authenticateAccount(authenticationRequest);
		
		Account account = result.getAccount();
		
		System.out.println(account.getFullName());
	}
}