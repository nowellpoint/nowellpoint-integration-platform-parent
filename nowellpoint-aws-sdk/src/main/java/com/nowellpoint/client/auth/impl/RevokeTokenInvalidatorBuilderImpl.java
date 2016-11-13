package com.nowellpoint.client.auth.impl;

import com.nowellpoint.client.auth.RevokeTokenInvalidatorBuilder;
import com.nowellpoint.client.auth.RevokeTokenRequest;

public class RevokeTokenInvalidatorBuilderImpl implements RevokeTokenInvalidatorBuilder {

	private String accessToken;

	@Override
	public RevokeTokenInvalidatorBuilder setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	@Override
	public RevokeTokenRequest build() {
		return new RevokeTokenRequestImpl(accessToken);
	}
}