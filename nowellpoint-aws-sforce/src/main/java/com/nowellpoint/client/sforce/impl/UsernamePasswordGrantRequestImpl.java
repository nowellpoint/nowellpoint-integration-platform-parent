package com.nowellpoint.client.sforce.impl;

import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;

public class UsernamePasswordGrantRequestImpl implements UsernamePasswordGrantRequest {
	
    public String clientId;
	
	public String clientSecret;
	
	private String username;
	
	private String password;
	
	private String securityToken;
	
	public UsernamePasswordGrantRequestImpl(String clientId, String clientSecret, String username, String password, String securityToken) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.username = username;
		this.password = password;
		this.securityToken = securityToken;
	}
	
	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public String getClientSecret() {
		return clientSecret;
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
	public String getSecurityToken() {
		return securityToken;
	}
}