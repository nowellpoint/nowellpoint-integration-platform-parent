package com.nowellpoint.okta;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {

	@JsonProperty(value="access_token")
	private String accessToken;
	
	@JsonProperty(value="token_type")
	private String tokenType;
	
	@JsonProperty(value="expires_in")
	private Long expiresIn;
	
	@JsonProperty(value="scope")
	private String scope;
	
	@JsonProperty(value="refresh_token")
	private String refreshToken;
	
	public Token() {
		
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public Long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}