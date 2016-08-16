package com.nowellpoint.client.sforce;

public interface RefreshTokenGrantAuthorizerBuilder {
	
	public RefreshTokenGrantAuthorizerBuilder setClientId(String clientId);
	
	public RefreshTokenGrantAuthorizerBuilder setClientSecret(String clientSecret);
	
	public RefreshTokenGrantAuthorizerBuilder setRefreshToken(String refreshToken);
	
	public RefreshTokenGrantRequest build();
}