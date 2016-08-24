package com.nowellpoint.client.auth.impl;

import com.nowellpoint.client.auth.PasswordGrantRequest;

public class PasswordGrantRequestImpl implements PasswordGrantRequest {
	
	private String username;
	
	private String password;
	
	public PasswordGrantRequestImpl(String username, String password) {
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
}