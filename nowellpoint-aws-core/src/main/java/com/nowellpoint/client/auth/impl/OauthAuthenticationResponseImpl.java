package com.nowellpoint.client.auth.impl;

import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.model.idp.Token;

public class OauthAuthenticationResponseImpl implements OauthAuthenticationResponse {
	
	private Token token;
	
	public OauthAuthenticationResponseImpl(Token token) {
		this.token = token;
	}
	
	@Override
	public Token getToken() {
		return token;
	}
}