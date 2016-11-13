package com.nowellpoint.client.sforce;

public class ThemeRequest {
	
	private String restEndpoint;
	
	private String accessToken;
	
	public ThemeRequest() {
		
	}

	public String getRestEndpoint() {
		return restEndpoint;
	}

	public void setRestEndpoint(String restEndpoint) {
		this.restEndpoint = restEndpoint;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public ThemeRequest withRestEndpoint(String restEndpoint) {
		setRestEndpoint(restEndpoint);
		return this;
	}
	
	public ThemeRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
}