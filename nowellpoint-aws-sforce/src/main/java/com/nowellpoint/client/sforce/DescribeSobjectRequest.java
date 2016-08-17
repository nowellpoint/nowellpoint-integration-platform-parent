package com.nowellpoint.client.sforce;

public class DescribeSobjectRequest {
	
	private String sobjectsUrl;
	
	private String accessToken;
	
	private String sobject;
	
	public DescribeSobjectRequest() {
		
	}

	public String getSobjectsUrl() {
		return sobjectsUrl;
	}

	public DescribeSobjectRequest withSobjectsUrl(String sobjectsUrl) {
		this.sobjectsUrl = sobjectsUrl;
		return this;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public DescribeSobjectRequest withAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public String getSobject() {
		return sobject;
	}

	public DescribeSobjectRequest withSobject(String sobject) {
		this.sobject = sobject;
		return this;
	}
}