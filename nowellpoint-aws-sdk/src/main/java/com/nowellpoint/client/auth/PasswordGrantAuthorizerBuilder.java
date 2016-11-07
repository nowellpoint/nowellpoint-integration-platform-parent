package com.nowellpoint.client.auth;

public interface PasswordGrantAuthorizerBuilder {
	
	public PasswordGrantAuthorizerBuilder setUsername(String username);
	
	public PasswordGrantAuthorizerBuilder setPassword(String password);
	
	public PasswordGrantRequest build();

}