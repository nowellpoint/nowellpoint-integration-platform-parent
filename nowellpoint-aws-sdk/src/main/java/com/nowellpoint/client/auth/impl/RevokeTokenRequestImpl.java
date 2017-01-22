package com.nowellpoint.client.auth.impl;

import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.model.Token;

public class RevokeTokenRequestImpl implements RevokeTokenRequest {
	
	private Token token;
	
	public RevokeTokenRequestImpl(Token token) {
		this.token = token;
	}

	@Override
	public Token getToken() {
		return token;
	}
}