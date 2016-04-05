package com.nowellpoint.client.sforce;

public class GetIdentityRequest {
	
	private String accessToken;
	
	private String id;
	
	public GetIdentityRequest() {
		
	}

	public String getAccessToken() {
		return accessToken;
	}

	public GetIdentityRequest setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public String getId() {
		return id;
	}

	public GetIdentityRequest setId(String id) {
		this.id = id;
		return this;
	}
}