package com.nowellpoint.client.auth.impl;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;

public class ClientCredentialsGrantRequestImpl implements ClientCredentialsGrantRequest {
	
	private String apiKeyId;
	
	private String apiKeySecret;
	
	private Environment environment;
	
	public ClientCredentialsGrantRequestImpl(Environment environment, String apiKeyId, String apiKeySecret) {
		this.environment = environment;
		this.apiKeyId = apiKeyId;
		this.apiKeySecret = apiKeySecret;
	}

	@Override
	public String getApiKeyId() {
		return apiKeyId;
	}

	@Override
	public String getApiKeySecret() {
		return apiKeySecret;
	}
	
	@Override
	public Environment getEnvironment() {
		return environment;
	}
}