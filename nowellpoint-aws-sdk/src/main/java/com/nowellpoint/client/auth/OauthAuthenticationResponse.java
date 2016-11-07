package com.nowellpoint.client.auth;

import com.nowellpoint.client.model.idp.Token;

public interface OauthAuthenticationResponse {
	
	public Token getToken();
}