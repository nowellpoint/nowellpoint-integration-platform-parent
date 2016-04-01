package com.nowellpoint.client.sforce;

public class AuthorizationGrantAuthorizerBuilder {
	
	private String code;
	
	public AuthorizationGrantAuthorizerBuilder setCode(String code) {
		this.code = code;
		return this;
	}
	
	public AuthorizationGrantRequest build() {
		return new AuthorizationGrantRequest(code);
	}

}
