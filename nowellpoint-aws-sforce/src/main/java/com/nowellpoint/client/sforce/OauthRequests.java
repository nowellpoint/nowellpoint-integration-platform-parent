package com.nowellpoint.client.sforce;

import com.nowellpoint.client.sforce.impl.AuthorizationGrantAuthorizerBuilderImpl;
import com.nowellpoint.client.sforce.impl.RefreshTokenGrantAuthorizerBuilderImpl;
import com.nowellpoint.client.sforce.impl.UsernamePasswordGrantAuthorizerBuilderImpl;

public class OauthRequests {
	
	public static final AuthorizationGrantRequestFactory AUTHORIZATION_GRANT_REQUEST = new AuthorizationGrantRequestFactory();
	public static final RefreshTokenGrantRequestFactory REFRESH_TOKEN_GRANT_REQUEST = new RefreshTokenGrantRequestFactory();
	public static final UsernamePasswordGrantRequestFactory USERNAME_PASSWORD_GRANT_REQUEST = new UsernamePasswordGrantRequestFactory();
	
	public static class AuthorizationGrantRequestFactory {
		public AuthorizationGrantAuthorizerBuilder builder() {
			AuthorizationGrantAuthorizerBuilder builder = new AuthorizationGrantAuthorizerBuilderImpl();
			return builder;
		}
	}
	
	public static class RefreshTokenGrantRequestFactory {
		public RefreshTokenGrantAuthorizerBuilder builder() {
			RefreshTokenGrantAuthorizerBuilder builder = new RefreshTokenGrantAuthorizerBuilderImpl();
			return builder;
		}
	}
	
	public static class UsernamePasswordGrantRequestFactory {
		public UsernamePasswordGrantAuthorizerBuilder builder() {
			UsernamePasswordGrantAuthorizerBuilder builder = new UsernamePasswordGrantAuthorizerBuilderImpl();
			return builder;
		}
	}
}