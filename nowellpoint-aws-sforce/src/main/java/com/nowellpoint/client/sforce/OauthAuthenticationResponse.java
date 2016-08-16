package com.nowellpoint.client.sforce;

import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;

public interface OauthAuthenticationResponse {
	
	public Token getToken();
	
	public Identity getIdentity();
}