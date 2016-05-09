package com.nowellpoint.client.sforce;

public interface UsernamePasswordGrantRequest {
	
	public String getClientId();
	
	public String getClientSecret();
	
	public String getUsername();
	
	public String getPassword();
	
	public String getSecurityToken();
}
