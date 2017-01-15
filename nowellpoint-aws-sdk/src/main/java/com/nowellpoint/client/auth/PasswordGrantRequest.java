package com.nowellpoint.client.auth;

import com.nowellpoint.client.Environment;

public interface PasswordGrantRequest {
	
	public String getUsername();
	
	public String getPassword();
	
	public Environment getEnvironment();

}