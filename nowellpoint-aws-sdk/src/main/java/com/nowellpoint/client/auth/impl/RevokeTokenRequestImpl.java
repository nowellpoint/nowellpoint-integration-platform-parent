package com.nowellpoint.client.auth.impl;

import com.nowellpoint.client.auth.RevokeTokenRequest;

public class RevokeTokenRequestImpl implements RevokeTokenRequest {
	
	private String accessToken;
	
	public RevokeTokenRequestImpl(String accessToken) {
		this.accessToken = accessToken;
	}

	@Override
	public String getAccessToken() {
		return accessToken;
	}
}