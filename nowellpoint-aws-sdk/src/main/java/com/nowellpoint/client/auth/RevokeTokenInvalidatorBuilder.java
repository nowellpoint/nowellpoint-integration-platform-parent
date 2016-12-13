package com.nowellpoint.client.auth;

public interface RevokeTokenInvalidatorBuilder {
	
	public RevokeTokenInvalidatorBuilder setAccessToken(String accessToken);
	
	public RevokeTokenRequest build();

}