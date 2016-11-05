package com.nowellpoint.client.sforce.impl;

import com.nowellpoint.client.sforce.PasswordGrantAuthorizerBuilder;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;

public class PasswordGrantAuthorizerBuilderImpl implements PasswordGrantAuthorizerBuilder {
	
	private String clientId;
	 
	private String clientSecret;
	
	private String username;
	
	private String password;
	
	private String securityToken;

	@Override
	public PasswordGrantAuthorizerBuilder setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	@Override
	public PasswordGrantAuthorizerBuilder setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}

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
	public PasswordGrantAuthorizerBuilder setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
		return this;
	}

	@Override
	public UsernamePasswordGrantRequest build() {
		return new UsernamePasswordGrantRequestImpl(clientId, clientSecret, username, password, securityToken);
	}
}