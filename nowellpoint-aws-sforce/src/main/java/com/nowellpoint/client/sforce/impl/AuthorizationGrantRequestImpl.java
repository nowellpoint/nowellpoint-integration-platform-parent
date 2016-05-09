package com.nowellpoint.client.sforce.impl;

import com.nowellpoint.client.sforce.AuthorizationGrantRequest;

public class AuthorizationGrantRequestImpl implements AuthorizationGrantRequest {
	
    public String clientId;
	
	public String clientSecret;
	
	public String callbackUri;
	
	private String code;
	
	public AuthorizationGrantRequestImpl(String clientId, String clientSecret, String callbackUri, String code) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.callbackUri = callbackUri;
		this.code = code;
	}
	
	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	@Override
	public String getCallbackUri() {
		return callbackUri;
	}
	
	@Override
	public String getCode() {
		return code;
	}
}