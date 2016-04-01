package com.nowellpoint.client.sforce;

public class OauthRequests {
	
	public static final AuthorizationGrantRequestFactory AUTHORIZATION_GRANT_REQUEST = new AuthorizationGrantRequestFactory();
	
	public static class AuthorizationGrantRequestFactory {
		public AuthorizationGrantAuthorizerBuilder builder() {
			AuthorizationGrantAuthorizerBuilder builder = new AuthorizationGrantAuthorizerBuilder();
			return builder;
		}
	}
}