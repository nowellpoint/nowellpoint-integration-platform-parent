package com.nowellpoint.client.sforce.impl;

import com.nowellpoint.client.sforce.RefreshTokenGrantAuthorizerBuilder;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;

public class RefreshTokenGrantAuthorizerBuilderImpl implements RefreshTokenGrantAuthorizerBuilder {
	
	private String clientId;
	 
	private String clientSecret;
	
	private String refreshToken;
	
	@Override
	public RefreshTokenGrantAuthorizerBuilder setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	@Override
	public RefreshTokenGrantAuthorizerBuilder setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}

	@Override
	public RefreshTokenGrantAuthorizerBuilder setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}
	
	@Override
	public RefreshTokenGrantRequest build() {
		return new RefreshTokenGrantRequestImpl(clientId, clientSecret, refreshToken);
	}
}