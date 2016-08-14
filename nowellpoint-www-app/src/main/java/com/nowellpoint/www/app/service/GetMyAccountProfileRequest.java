package com.nowellpoint.www.app.service;

public class GetMyAccountProfileRequest {
	
	private String accessToken;
	
	public GetMyAccountProfileRequest() {
		
	}

	public String getAccessToken() {
		return accessToken;
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public GetMyAccountProfileRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
}