package com.nowellpoint.client.sforce;

public class DescribeSobjectsRequest {
	
	private String sobjectsUrl;
	
	private String accessToken;
	
	public DescribeSobjectsRequest() {
		
	}

	public String getSobjectUrl() {
		return sobjectsUrl;
	}

	public DescribeSobjectsRequest setSobjectUrl(String sobjectsUrl) {
		this.sobjectsUrl = sobjectsUrl;
		return this;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public DescribeSobjectsRequest setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}
}