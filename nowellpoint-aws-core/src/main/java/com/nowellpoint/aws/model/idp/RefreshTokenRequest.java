package com.nowellpoint.aws.model.idp;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class RefreshTokenRequest extends AbstractLambdaRequest {
	
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