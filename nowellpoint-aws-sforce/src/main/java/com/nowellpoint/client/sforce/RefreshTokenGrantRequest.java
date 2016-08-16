package com.nowellpoint.client.sforce;

public interface RefreshTokenGrantRequest {
	
	public String getClientId();
	
	public String getClientSecret();
	
	public String getRefreshToken();
}
