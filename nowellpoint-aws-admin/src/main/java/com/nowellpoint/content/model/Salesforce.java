package com.nowellpoint.content.model;

public class Salesforce {
	
	private String apiVersion;
	private String clientId;
	private String clientSecret;
	private Urls urls;
	
	public Salesforce() {
		
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public Urls getUrls() {
		return urls;
	}
}