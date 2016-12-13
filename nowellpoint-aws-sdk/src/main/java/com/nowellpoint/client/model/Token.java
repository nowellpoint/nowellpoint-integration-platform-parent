package com.nowellpoint.client.model;

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

	@Override
	public String toString() {
		return "Token [access_token=" + access_token + ", refresh_token="
				+ refresh_token + ", token_type=" + token_type
				+ ", expires_in=" + expires_in + "]";
	}
}