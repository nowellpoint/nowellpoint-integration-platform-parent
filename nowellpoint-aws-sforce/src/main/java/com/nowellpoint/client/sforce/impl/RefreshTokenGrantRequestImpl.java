package com.nowellpoint.client.sforce.impl;

import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;

public class RefreshTokenGrantRequestImpl implements RefreshTokenGrantRequest {
	
    public String clientId;
	
	public String clientSecret;
	
	private String refreshToken;
	
	public RefreshTokenGrantRequestImpl(String clientId, String clientSecret, String refreshToken) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.refreshToken = refreshToken;
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
	public String getRefreshToken() {
		return refreshToken;
	}
}