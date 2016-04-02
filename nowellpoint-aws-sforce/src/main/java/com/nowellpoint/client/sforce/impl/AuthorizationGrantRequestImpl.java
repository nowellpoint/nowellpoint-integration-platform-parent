package com.nowellpoint.client.sforce.impl;

import com.nowellpoint.client.sforce.AuthorizationGrantRequest;

public class AuthorizationGrantRequestImpl implements AuthorizationGrantRequest {
	
	private String code;
	
	public AuthorizationGrantRequestImpl(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}