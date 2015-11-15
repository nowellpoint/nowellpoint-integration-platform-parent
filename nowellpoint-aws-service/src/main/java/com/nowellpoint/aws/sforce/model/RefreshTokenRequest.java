package com.nowellpoint.aws.sforce.model;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractRequest;

public class RefreshTokenRequest extends AbstractRequest implements Serializable {
	
	private static final long serialVersionUID = -3186641818593860386L;
	
	private String refreshToken;
	
	public RefreshTokenRequest() {
		
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public RefreshTokenRequest withRefreshToken(String refreshToken) {
		setRefreshToken(refreshToken);
		return this;
	}
}