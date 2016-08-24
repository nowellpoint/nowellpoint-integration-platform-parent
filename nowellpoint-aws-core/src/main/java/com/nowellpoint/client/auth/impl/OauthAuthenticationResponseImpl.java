package com.nowellpoint.client.auth.impl;

import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;

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