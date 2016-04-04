package com.nowellpoint.client.sforce.impl;

import com.nowellpoint.client.sforce.UsernamePasswordGrantAuthorizerBuilder;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;

public class UsernamePasswordGrantAuthorizerBuilderImpl implements UsernamePasswordGrantAuthorizerBuilder {
	
	private String clientId;
	 
	private String clientSecret;
	
	private String username;
	
	private String password;
	
	private String securityToken;

	@Override
	public UsernamePasswordGrantAuthorizerBuilder setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	@Override
	public UsernamePasswordGrantAuthorizerBuilder setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}

	@Override
	public UsernamePasswordGrantAuthorizerBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	@Override
	public UsernamePasswordGrantAuthorizerBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

	@Override
	public UsernamePasswordGrantAuthorizerBuilder setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
		return this;
	}

	@Override
	public UsernamePasswordGrantRequest build() {
		return new UsernamePasswordGrantRequestImpl(clientId, clientSecret, username, password, securityToken);
	}
}