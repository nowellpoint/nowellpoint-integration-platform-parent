package com.nowellpoint.api.dto.idp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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
}