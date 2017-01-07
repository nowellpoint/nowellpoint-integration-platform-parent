package com.nowellpoint.client.auth;

public interface PasswordCredentials extends Credentials {
	
	public String getUsername();
	
	public String getPassword();
}