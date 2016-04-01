package com.nowellpoint.client.sforce;

import com.nowellpoint.client.sforce.model.Token;

public interface OauthAuthorizationGrantResponse {
	
	public Token getToken();
}