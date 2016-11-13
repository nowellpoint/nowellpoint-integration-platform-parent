package com.nowellpoint.client.auth;

public interface ClientCredentialsGrantAuthorizerBuilder {
	
	public ClientCredentialsGrantAuthorizerBuilder setApiKeyId(String apiKeyId);
	
	public ClientCredentialsGrantAuthorizerBuilder setApiKeySecret(String apiKeySecret);
	
	public ClientCredentialsGrantRequest build();

}