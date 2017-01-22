package com.nowellpoint.client.auth;

import com.nowellpoint.client.model.Token;

public interface RevokeTokenInvalidatorBuilder {
	
	public RevokeTokenInvalidatorBuilder setToken(Token token);
	
	public RevokeTokenRequest build();

}