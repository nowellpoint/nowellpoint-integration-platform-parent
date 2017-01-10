package com.nowellpoint.client.auth;

import com.nowellpoint.client.Environment;

public interface ClientCredentialsGrantRequest {
	
	public String getApiKeyId();
	
	public String getApiKeySecret();
	
	public Environment getEnvironment();

}