package com.nowellpoint.aws.idp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Token implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 7118882001234268808L;
	
	private String access_token;
	
	private String refresh_token;
	
	private String token_type;
	
	private Long expires_in;
	
	private String stormpath_access_token_href;
	
	public Token() {
		
	}
	
	private Token(TokenBuilder builder) {
		this.access_token = builder.access_token;
		this.expires_in = builder.expires_in;
		this.refresh_token = builder.refresh_token;
		this.stormpath_access_token_href = builder.stormpath_access_token_href;
		this.token_type = builder.token_type;
	}

	public String getAccessToken() {
		return access_token;
	}

	public void setAccessToken(String accessToken) {
		this.access_token = accessToken;
	}

	public String getRefreshToken() {
		return refresh_token;
	}

	public void setRefreshToken(String refreshToken) {
		this.refresh_token = refreshToken;
	}

	public String getTokenType() {
		return token_type;
	}

	public void setTokenType(String tokenType) {
		this.token_type = tokenType;
	}

	public Long getExpiresIn() {
		return expires_in;
	}

	public void setExpiresIn(Long expiresIn) {
		this.expires_in = expiresIn;
	}

	public String getStormpathAccessTokenHref() {
		return stormpath_access_token_href;
	}

	public void setStormpathAccessTokenHref(String stormpathAccessTokenHref) {
		this.stormpath_access_token_href = stormpathAccessTokenHref;
	}

	@Override
	public String toString() {
		return "Token [access_token=" + access_token + ", refresh_token="
				+ refresh_token + ", token_type=" + token_type
				+ ", expires_in=" + expires_in
				+ ", stormpath_access_token_href="
				+ stormpath_access_token_href + "]";
	}
	
	public static TokenBuilder builder() {
		return new Token().new TokenBuilder();
	}
	
	public class TokenBuilder {
		
		private String access_token;
		
		private String refresh_token;
		
		private String token_type;
		
		private Long expires_in;
		
		private String stormpath_access_token_href;

		public TokenBuilder setAccessToken(String access_token) {
			this.access_token = access_token;
			return this;
		}

		public TokenBuilder setRefreshToken(String refresh_token) {
			this.refresh_token = refresh_token;
			return this;
		}

		public TokenBuilder setTokenType(String token_type) {
			this.token_type = token_type;
			return this;
		}

		public TokenBuilder setExpiresIn(Long expires_in) {
			this.expires_in = expires_in;
			return this;
		}

		public TokenBuilder setStormpathAccessTokenHref(String stormpath_access_token_href) {
			this.stormpath_access_token_href = stormpath_access_token_href;
			return this;
		}
		
		public Token build() {
			return new Token(this);
		}
	}
}