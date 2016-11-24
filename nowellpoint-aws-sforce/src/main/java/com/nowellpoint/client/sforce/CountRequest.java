package com.nowellpoint.client.sforce;

public class CountRequest {
	
	private String accessToken;

	private String queryUrl;
	
	private String queryString;
	
	public CountRequest() {
		
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getQueryUrl() {
		return queryUrl;
	}

	public void setQueryUrl(String queryUrl) {
		this.queryUrl = queryUrl;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	public CountRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
	
	public CountRequest withQueryUrl(String queryUrl) {
		setQueryUrl(queryUrl);
		return this;
	}
	
	public CountRequest withQueryString(String queryString) {
		setQueryString(queryString);
		return this;
	}
}