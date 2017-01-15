package com.nowellpoint.client.auth.impl;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.auth.ClientCredentialsGrantAuthorizerBuilder;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;

public class ClientCredentialsGrantAuthorizerBuilderImpl implements ClientCredentialsGrantAuthorizerBuilder {

	private String apiKeyId;
	
	private String apiKeySecret;
	
	private Environment environment;

	@Override
	public ClientCredentialsGrantAuthorizerBuilder setApiKeyId(String apiKeyId) {
		this.apiKeyId = apiKeyId;
		return this;
	}

	@Override
	public ClientCredentialsGrantAuthorizerBuilder setApiKeySecret(String apiKeySecret) {
		this.apiKeySecret = apiKeySecret;
		return this;
	}
	
	@Override
	public ClientCredentialsGrantAuthorizerBuilder setEnvironment(Environment environment) {
		this.environment = environment;
		return this;
	}
	
	@Override
	public ClientCredentialsGrantRequest build() {
		return new ClientCredentialsGrantRequestImpl(environment, apiKeyId, apiKeySecret);
	}
}