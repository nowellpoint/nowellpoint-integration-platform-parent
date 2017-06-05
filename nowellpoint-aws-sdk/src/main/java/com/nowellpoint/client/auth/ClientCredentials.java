package com.nowellpoint.client.auth;

public interface ClientCredentials extends Credentials {
	
	public String getApiKeyId();
	
	public String getApiKeySecret();
}