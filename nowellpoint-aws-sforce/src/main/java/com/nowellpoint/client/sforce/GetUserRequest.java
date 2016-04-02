package com.nowellpoint.client.sforce;

public class GetUserRequest {
	
	private String sobjectUrl;
	
	private String accessToken;
	
	private String userId;
	
	public GetUserRequest() {
		
	}

	public String getSobjectUrl() {
		return sobjectUrl;
	}

	public GetUserRequest setSobjectUrl(String sobjectUrl) {
		this.sobjectUrl = sobjectUrl;
		return this;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public GetUserRequest setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public GetUserRequest setUserId(String userId) {
		this.userId = userId;
		return this;
	}
}