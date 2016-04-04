package com.nowellpoint.client.sforce;

public interface UsernamePasswordGrantAuthorizerBuilder {
	
	public UsernamePasswordGrantAuthorizerBuilder setClientId(String clientId);
	
	public UsernamePasswordGrantAuthorizerBuilder setClientSecret(String clientSecret);
	
	public UsernamePasswordGrantAuthorizerBuilder setUsername(String username);
	
	public UsernamePasswordGrantAuthorizerBuilder setPassword(String password);
	
	public UsernamePasswordGrantAuthorizerBuilder setSecurityToken(String securityToken);
	
	public UsernamePasswordGrantRequest build();
}