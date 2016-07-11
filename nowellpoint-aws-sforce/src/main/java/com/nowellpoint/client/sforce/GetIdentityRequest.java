package com.nowellpoint.client.sforce;

public class GetIdentityRequest {
	
	private String accessToken;
	
	private String id;
	
	private String instance;
	
	private String organizationId;
	
	private String userId;
	
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
	
	public String getInstance() {
		return instance;
	}
	
	public GetIdentityRequest setInstance(String instance) {
		this.instance = instance;
		return this;
	}
	
	public String getOrganizationId() {
		return organizationId;
	}
	
	public GetIdentityRequest setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
		return this;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public GetIdentityRequest setUserId(String userId) {
		this.userId = userId;
		return this;
	}
}