package com.nowellpoint.aws.model.sforce;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class RefreshTokenRequest extends AbstractLambdaRequest implements Serializable {
	
	private static final long serialVersionUID = -3186641818593860386L;
	
	private String refreshTokenUri;
	
	private String clientId;
	
	private String clientSecret;
	
	private String refreshToken;
	
	public RefreshTokenRequest() {
		
	}
	
	public String getRefreshTokenUri() {
		return refreshTokenUri;
	}

	public void setRefreshTokenUri(String refreshTokenUri) {
		this.refreshTokenUri = refreshTokenUri;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
	
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public RefreshTokenRequest withRefreshTokenUri(String refreshTokenUri) {
		setRefreshTokenUri(refreshTokenUri);
		return this;
	}
	
	public RefreshTokenRequest withClientId(String clientId) {
		setClientId(clientId);
		return this;
	}
	
	public RefreshTokenRequest withClientSecret(String clientSecret) {
		setClientSecret(clientSecret);
		return this;
	}
	
	public RefreshTokenRequest withRefreshToken(String refreshToken) {
		setRefreshToken(refreshToken);
		return this;
	}
}