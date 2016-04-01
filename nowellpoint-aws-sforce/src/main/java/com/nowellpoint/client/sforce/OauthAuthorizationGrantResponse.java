package com.nowellpoint.client.sforce;

import com.nowellpoint.client.sforce.model.Token;

public class OauthAuthorizationGrantResponse {
	
	private Token token;
	
	public OauthAuthorizationGrantResponse(Token token) {
		this.token = token;
	}
	
	public Token getToken() {
		return token;
	}
}