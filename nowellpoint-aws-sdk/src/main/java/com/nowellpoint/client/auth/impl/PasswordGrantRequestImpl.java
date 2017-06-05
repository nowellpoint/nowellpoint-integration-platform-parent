package com.nowellpoint.client.auth.impl;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.auth.PasswordGrantRequest;

public class PasswordGrantRequestImpl implements PasswordGrantRequest {
	
	private String username;
	
	private String password;
	
	private Environment environment;
	
	public PasswordGrantRequestImpl(Environment environment, String username, String password) {
		this.environment = environment;
		this.username = username;
		this.password = password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public Environment getEnvironment() {
		return environment;
	}
}