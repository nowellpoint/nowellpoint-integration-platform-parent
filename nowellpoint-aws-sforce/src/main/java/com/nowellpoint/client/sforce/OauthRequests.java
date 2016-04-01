package com.nowellpoint.client.sforce;

public class OauthRequests {
	
	public static final AuthorizationGrantRequestFactory AUTHORIZATION_GRANT_REQUEST = new AuthorizationGrantRequestFactory();
	
	class AuthorizationGrantRequestImpl implements AuthorizationGrantRequest {
		private String code;
		
		public AuthorizationGrantRequestImpl(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return code;
		}
	}
	
	public static class AuthorizationGrantRequestFactory {
		public AuthorizationGrantAuthorizerBuilder builder() {
			OauthRequests request = new OauthRequests();
			AuthorizationGrantAuthorizerBuilder builder = request.new AuthorizationGrantAuthorizerBuilderImpl();
			return builder;
		}
	}
	
	class AuthorizationGrantAuthorizerBuilderImpl implements AuthorizationGrantAuthorizerBuilder {
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
}