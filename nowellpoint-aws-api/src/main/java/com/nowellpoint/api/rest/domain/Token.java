package com.nowellpoint.api.rest.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class Token {
	
	private String id;
	
	private String environment_url;

	private String access_token;
	
	private String refresh_token;
	
	private String token_type;
	
	private Long expires_in;
	
	public Token() {
		
	}

	@JsonProperty(value="id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty(value="environment_url")
	public String getEnvironmentUrl() {
		return environment_url;
	}

	public void setEnvironmentUrl(String environmentUrl) {
		this.environment_url = environmentUrl;
	}

	@JsonProperty(value="access_token")
	public String getAccessToken() {
		return access_token;
	}

	public void setAccessToken(String accessToken) {
		this.access_token = accessToken;
	}

	@JsonProperty(value="refresh_token")
	public String getRefreshToken() {
		return refresh_token;
	}

	public void setRefreshToken(String refreshToken) {
		this.refresh_token = refreshToken;
	}

	@JsonProperty(value="token_type")
	public String getTokenType() {
		return token_type;
	}

	public void setTokenType(String tokenType) {
		this.token_type = tokenType;
	}

	@JsonProperty(value="expires_in")
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