package com.nowellpoint.client.sforce;

public interface AuthorizationGrantAuthorizerBuilder {
	
	public AuthorizationGrantAuthorizerBuilder setClientId(String clientId);
	
	public AuthorizationGrantAuthorizerBuilder setClientSecret(String clientSecret);
	
	public AuthorizationGrantAuthorizerBuilder setCallbackUri(String callbackUri);
	
	public AuthorizationGrantAuthorizerBuilder setCode(String code);
	
	public AuthorizationGrantRequest build();
}