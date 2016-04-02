package com.nowellpoint.client.sforce.impl;

import com.nowellpoint.client.sforce.OauthAuthorizationGrantResponse;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;

public class OauthAuthorizationGrantResponseImpl implements OauthAuthorizationGrantResponse{
	
	private Token token;
	
	private Identity identity;
	
	public OauthAuthorizationGrantResponseImpl(Token token, Identity identity) {
		this.token = token;
		this.identity = identity;
	}
	
	public Token getToken() {
		return token;
	}
	
	public Identity getIdentity() {
		return identity;
	}
}