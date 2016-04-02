package com.nowellpoint.client.sforce.impl;

import com.nowellpoint.client.sforce.AuthorizationGrantAuthorizerBuilder;
import com.nowellpoint.client.sforce.AuthorizationGrantRequest;

public class AuthorizationGrantAuthorizerBuilderImpl implements AuthorizationGrantAuthorizerBuilder {
	private String code;
	
	@Override
	public AuthorizationGrantAuthorizerBuilderImpl setCode(String code) {
		this.code = code;
		return this;
	}
	
	@Override
	public AuthorizationGrantRequest build() {
		return new AuthorizationGrantRequestImpl(code);
	}
}