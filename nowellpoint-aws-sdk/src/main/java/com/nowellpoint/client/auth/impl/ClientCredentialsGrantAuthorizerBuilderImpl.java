package com.nowellpoint.client.auth.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.auth.ClientCredentialsGrantAuthorizerBuilder;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;
import com.nowellpoint.client.util.MessageProvider;
import com.nowellpoint.util.Assert;

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
		if (Assert.isNullOrEmpty(apiKeyId) || Assert.isNullOrEmpty(apiKeySecret) || Assert.isNull(environment)) {
			
			List<String> errors = new ArrayList<>();
			if (Assert.isNullOrEmpty(apiKeyId)) {
				errors.add(MessageProvider.getMessage(Locale.US, "api.key.id.required"));
			}
			if (Assert.isNullOrEmpty(apiKeySecret)) {
				errors.add(MessageProvider.getMessage(Locale.US, "api.key.secret.required"));
			}
			if (Assert.isNull(environment)) {
				errors.add(MessageProvider.getMessage(Locale.US, "environment.required"));
			}
			
			String error = errors.stream().collect(Collectors.joining(" / "));
			
			throw new IllegalArgumentException(error);
		}
		
		return new ClientCredentialsGrantRequestImpl(environment, apiKeyId, apiKeySecret);
	}
}