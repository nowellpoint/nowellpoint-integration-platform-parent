package com.nowellpoint.client.auth.impl;

import com.nowellpoint.client.auth.RevokeTokenInvalidatorBuilder;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.model.Token;

public class RevokeTokenInvalidatorBuilderImpl implements RevokeTokenInvalidatorBuilder {

	private Token token;

	@Override
	public RevokeTokenInvalidatorBuilder setToken(Token token) {
		this.token = token;
		return this;
	}

	@Override
	public RevokeTokenRequest build() {
		return new RevokeTokenRequestImpl(token);
	}
}