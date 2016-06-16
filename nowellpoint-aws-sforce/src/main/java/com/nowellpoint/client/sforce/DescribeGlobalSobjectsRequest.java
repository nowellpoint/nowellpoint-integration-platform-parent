package com.nowellpoint.client.sforce;

public class DescribeGlobalSobjectsRequest {
	
	private String sobjectsUrl;
	
	private String accessToken;
	
	public DescribeGlobalSobjectsRequest() {
		
	}

	public String getSobjectsUrl() {
		return sobjectsUrl;
	}

	public DescribeGlobalSobjectsRequest setSobjectsUrl(String sobjectsUrl) {
		this.sobjectsUrl = sobjectsUrl;
		return this;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public DescribeGlobalSobjectsRequest setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}
}