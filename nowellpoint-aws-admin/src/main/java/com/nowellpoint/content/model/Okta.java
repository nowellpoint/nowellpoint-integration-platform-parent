package com.nowellpoint.content.model;

public class Okta {
	
	private String apiKey;
	private String groupId;
	private String orgUrl;
	private String clientId;
	private String clientSecret;
	private String authorizationServer;
	
	public Okta() {
		
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getOrgUrl() {
		return orgUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getAuthorizationServer() {
		return authorizationServer;
	}
}