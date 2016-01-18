package com.nowellpoint.aws.idp.model;

import org.hibernate.validator.constraints.NotEmpty;

public class RevokeTokenRequest extends AbstractIdpRequest {
	
	private static final long serialVersionUID = 2302532913990733635L;
	
	private String accessToken;
	
	public RevokeTokenRequest() {
		
	}

	@NotEmpty
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	@NotEmpty
	public String getApiEndpoint() {
		return super.getApiEndpoint();
	}
	
	public void setApiEndpoint(String apiEndpoint) {
		super.setApiEndpoint(apiEndpoint);
	}
	
	public RevokeTokenRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
	
	public RevokeTokenRequest withApiEndpoint(String apiEndpoint) {
		setApiEndpoint(apiEndpoint);
		return this;
	}
	
	public RevokeTokenRequest withApiKeyId(String apiKeyId) {
		setApiKeyId(apiKeyId);
		return this;
	}
	
	public RevokeTokenRequest withApiKeySecret(String apiKeySecret) {
		setApiKeySecret(apiKeySecret);
		return this;
	}
}