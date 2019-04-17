package com.nowellpoint.client.sforce.impl;

import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
//import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;

public class OauthAuthenticationResponseImpl implements OauthAuthenticationResponse {
	
	private Token token;
	
	//private Identity identity;
	
	public OauthAuthenticationResponseImpl(Token token) {
		this.token = token;
		//this.identity = identity;
	}
	
	@Override
	public Token getToken() {
		return token;
	}
	
//	public Identity getIdentity() {
//		return identity;
//	}
}