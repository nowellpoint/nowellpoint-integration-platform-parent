package com.nowellpoint.client.auth;

public interface UsernamePasswordCredentials extends Credentials {
	
	public String getUsername();
	
	public String getPassword();
}