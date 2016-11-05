package com.nowellpoint.client.sforce;

public interface PasswordGrantAuthorizerBuilder {
	
	public PasswordGrantAuthorizerBuilder setClientId(String clientId);
	
	public PasswordGrantAuthorizerBuilder setClientSecret(String clientSecret);
	
	public PasswordGrantAuthorizerBuilder setUsername(String username);
	
	public PasswordGrantAuthorizerBuilder setPassword(String password);
	
	public PasswordGrantAuthorizerBuilder setSecurityToken(String securityToken);
	
	public UsernamePasswordGrantRequest build();
}