package com.nowellpoint.www.app.service;

public class GetSalesforceConnectorRequest {
	
	private String accessToken;
	private String id;
	
	public GetSalesforceConnectorRequest() {
		
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
	
	public GetSalesforceConnectorRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
	
	public GetSalesforceConnectorRequest withId(String id) {
		setId(id);
		return this;
	}
}