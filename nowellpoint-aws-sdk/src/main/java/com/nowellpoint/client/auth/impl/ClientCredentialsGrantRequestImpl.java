package com.nowellpoint.client.auth.impl;

import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;

public class ClientCredentialsGrantRequestImpl implements ClientCredentialsGrantRequest {
	
	private String apiKeyId;
	
	private String apiKeySecret;
	
	public ClientCredentialsGrantRequestImpl(String apiKeyId, String apiKeySecret) {
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
}