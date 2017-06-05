package com.nowellpoint.client.model;

public class CreateSalesforceConnectorRequest {
	
	private String accessToken;
	
	private String id;
	
	private String instanceUrl;
	
	private String refreshToken;
	
	public CreateSalesforceConnectorRequest() {
		
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInstanceUrl() {
		return instanceUrl;
	}

	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public CreateSalesforceConnectorRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
	
	public CreateSalesforceConnectorRequest withId(String id) {
		setId(id);
		return this;
	}
	
	public CreateSalesforceConnectorRequest withInstanceUrl(String instanceUrl) {
		setInstanceUrl(instanceUrl);
		return this;
	}
	
	public CreateSalesforceConnectorRequest withRefreshToken(String refreshToken) {
		setRefreshToken(refreshToken);
		return this;
	}
}