package com.nowellpoint.client.auth;

import com.nowellpoint.client.auth.impl.PasswordGrantAuthorizerBuilderImpl;

public class OauthRequests {
	
	public static final PasswordGrantRequestFactory PASSWORD_GRANT_REQUEST = new PasswordGrantRequestFactory();
	
	public static class PasswordGrantRequestFactory {
		public PasswordGrantAuthorizerBuilder builder() {
			PasswordGrantAuthorizerBuilder builder = new PasswordGrantAuthorizerBuilderImpl();
			return builder;
		}
	}
}