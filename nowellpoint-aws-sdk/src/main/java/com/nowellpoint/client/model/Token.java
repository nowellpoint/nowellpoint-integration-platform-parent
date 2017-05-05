package com.nowellpoint.client.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Token implements Serializable {
	
	private static final long serialVersionUID = 7118882001234268808L;
	
	private String id;
	
	private String environment_url;
	
	private String access_token;
	
	private String refresh_token;
	
	private String token_type;
	
	private Long expires_in;
	
	public Token() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEnvironmentUrl() {
		return environment_url;
	}

	public void setEnvironmentUrl(String environmentUrl) {
		this.environment_url = environmentUrl;
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
	
	public void logout() {
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setToken(this)
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);	
		
		setId(null);
		setEnvironmentUrl(null);
		setAccessToken(null);
		setRefreshToken(null);
		setTokenType(null);
		setExpiresIn(null);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}