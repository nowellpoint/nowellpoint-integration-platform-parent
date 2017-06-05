package com.nowellpoint.client.auth;

import com.nowellpoint.client.Environment;

public interface PasswordGrantAuthorizerBuilder {
	
	public PasswordGrantAuthorizerBuilder setUsername(String username);
	
	public PasswordGrantAuthorizerBuilder setPassword(String password);
	
	public PasswordGrantAuthorizerBuilder setEnvironment(Environment environment);
	
	public PasswordGrantRequest build();

}