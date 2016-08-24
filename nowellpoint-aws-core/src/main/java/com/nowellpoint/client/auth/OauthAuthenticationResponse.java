package com.nowellpoint.client.auth;

import com.nowellpoint.aws.idp.model.Token;

public interface OauthAuthenticationResponse {
	
	public Token getToken();
}