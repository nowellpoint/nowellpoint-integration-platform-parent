package com.nowellpoint.client.sforce.impl;

import com.nowellpoint.client.sforce.AuthorizationGrantAuthorizerBuilder;
import com.nowellpoint.client.sforce.AuthorizationGrantRequest;

public class AuthorizationGrantAuthorizerBuilderImpl implements AuthorizationGrantAuthorizerBuilder {
	
	private String clientId;
	 
	private String clientSecret;
		
	private String callbackUri;
	
	private String code;
	
	@Override
	public AuthorizationGrantAuthorizerBuilder setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	@Override
	public AuthorizationGrantAuthorizerBuilder setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}

	@Override
	public AuthorizationGrantAuthorizerBuilder setCallbackUri(String callbackUri) {
		this.callbackUri = callbackUri;
		return this;
	}

	@Override
	public AuthorizationGrantAuthorizerBuilderImpl setCode(String code) {
		this.code = code;
		return this;
	}
	
	@Override
	public AuthorizationGrantRequest build() {
		return new AuthorizationGrantRequestImpl(clientId, clientSecret, callbackUri, code);
	}
}