package com.nowellpoint.client.auth;

import com.nowellpoint.client.model.idp.Token;

public class TokenCredentials implements Credentials {
	
	private Token token;
	
	public TokenCredentials(Token token) {
		this.token = token;
	}
	
	public Token getToken() {
		return token;
	}
}