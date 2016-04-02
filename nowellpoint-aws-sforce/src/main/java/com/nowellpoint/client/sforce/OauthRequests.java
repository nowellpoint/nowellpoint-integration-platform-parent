package com.nowellpoint.client.sforce;

import com.nowellpoint.client.sforce.impl.AuthorizationGrantAuthorizerBuilderImpl;

public class OauthRequests {
	
	public static final AuthorizationGrantRequestFactory AUTHORIZATION_GRANT_REQUEST = new AuthorizationGrantRequestFactory();
	
	public static class AuthorizationGrantRequestFactory {
		public AuthorizationGrantAuthorizerBuilder builder() {
			AuthorizationGrantAuthorizerBuilder builder = new AuthorizationGrantAuthorizerBuilderImpl();
			return builder;
		}
	}
}