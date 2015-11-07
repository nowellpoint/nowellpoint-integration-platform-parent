package com.nowellpoint.aws.lambda.idp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Token implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 7118882001234268808L;
	
	@JsonProperty(value="access_token")
	private String access_token;
	
	@JsonProperty(value="refresh_token")
	private String refresh_token;
	
	@JsonProperty(value="token_type")
	private String token_type;
	
	@JsonProperty(value="expires_in")
	private Long expires_in;
	
	@JsonProperty(value="stormpath_access_token_href")
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
}