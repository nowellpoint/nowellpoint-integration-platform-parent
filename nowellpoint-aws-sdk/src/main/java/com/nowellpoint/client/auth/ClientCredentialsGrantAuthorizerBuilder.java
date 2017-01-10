package com.nowellpoint.client.auth;

import com.nowellpoint.client.Environment;

public interface ClientCredentialsGrantAuthorizerBuilder {
	
	public ClientCredentialsGrantAuthorizerBuilder setApiKeyId(String apiKeyId);
	
	public ClientCredentialsGrantAuthorizerBuilder setApiKeySecret(String apiKeySecret);
	
	public ClientCredentialsGrantAuthorizerBuilder setEnvionrment(Environment environment);
	
	public ClientCredentialsGrantRequest build();

}