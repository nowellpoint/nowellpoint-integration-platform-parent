package com.nowellpoint.client.sforce;

public interface AuthorizationGrantRequest {
	
	public String getClientId();
	
	public String getClientSecret();
	
	public String getCallbackUri();
	
	public String getCode();
}
