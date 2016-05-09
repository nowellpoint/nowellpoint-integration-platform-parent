package com.nowellpoint.client.sforce;

public class GetOrganizationRequest {
	
	private String sobjectUrl;
	
	private String accessToken;
	
	private String organizationId;
	
	public GetOrganizationRequest() {
		
	}

	public String getSobjectUrl() {
		return sobjectUrl;
	}

	public GetOrganizationRequest setSobjectUrl(String sobjectUrl) {
		this.sobjectUrl = sobjectUrl;
		return this;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public GetOrganizationRequest setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public GetOrganizationRequest setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
		return this;
	}
}