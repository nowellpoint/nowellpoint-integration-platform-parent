package com.nowellpoint.client.sforce;

public interface AuthorizationGrantAuthorizerBuilder {
	
	public AuthorizationGrantAuthorizerBuilder setCode(String code);
	
	public AuthorizationGrantRequest build();
}