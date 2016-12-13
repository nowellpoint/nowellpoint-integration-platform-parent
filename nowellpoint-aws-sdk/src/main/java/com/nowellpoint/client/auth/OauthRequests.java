package com.nowellpoint.client.auth;

import com.nowellpoint.client.auth.impl.ClientCredentialsGrantAuthorizerBuilderImpl;
import com.nowellpoint.client.auth.impl.PasswordGrantAuthorizerBuilderImpl;
import com.nowellpoint.client.auth.impl.RevokeTokenInvalidatorBuilderImpl;

public class OauthRequests {
	
	public static final PasswordGrantRequestFactory PASSWORD_GRANT_REQUEST = new PasswordGrantRequestFactory();
	public static final ClientCredentialsGrantRequestFactory CLIENT_CREDENTIALS_GRANT_REQUEST = new ClientCredentialsGrantRequestFactory();
	public static final RevokeTokenRequestFactory REVOKE_TOKEN_REQUEST = new RevokeTokenRequestFactory();
	
	public static class PasswordGrantRequestFactory {
		public PasswordGrantAuthorizerBuilder builder() {
			PasswordGrantAuthorizerBuilder builder = new PasswordGrantAuthorizerBuilderImpl();
			return builder;
		}
	}
	
	public static class ClientCredentialsGrantRequestFactory {
		public ClientCredentialsGrantAuthorizerBuilder builder() {
			ClientCredentialsGrantAuthorizerBuilder builder = new ClientCredentialsGrantAuthorizerBuilderImpl();
			return builder;
		}
	}
	
	public static class RevokeTokenRequestFactory {
		public RevokeTokenInvalidatorBuilder builder() {
			RevokeTokenInvalidatorBuilder builder = new RevokeTokenInvalidatorBuilderImpl();
			return builder;
		}
	}
}