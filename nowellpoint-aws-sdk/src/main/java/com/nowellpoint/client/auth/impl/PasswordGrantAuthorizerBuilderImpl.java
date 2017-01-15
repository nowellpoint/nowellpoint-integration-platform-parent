package com.nowellpoint.client.auth.impl;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.auth.PasswordGrantAuthorizerBuilder;
import com.nowellpoint.client.auth.PasswordGrantRequest;

public class PasswordGrantAuthorizerBuilderImpl implements PasswordGrantAuthorizerBuilder {

	private String username;
	
	private String password;
	
	private Environment environment;

	@Override
	public PasswordGrantAuthorizerBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	@Override
	public PasswordGrantAuthorizerBuilder setPassword(String password) {
		this.password = password;
		return this;
	}
	
	@Override
	public PasswordGrantAuthorizerBuilder setEnvironment(Environment environment) {
		this.environment = environment;
		return this;
	}
	
	@Override
	public PasswordGrantRequest build() {
		return new PasswordGrantRequestImpl(environment, username, password);
	}
}