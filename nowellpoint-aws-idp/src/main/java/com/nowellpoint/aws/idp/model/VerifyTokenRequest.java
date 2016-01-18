package com.nowellpoint.aws.idp.model;

import org.hibernate.validator.constraints.NotEmpty;

public class VerifyTokenRequest extends AbstractIdpRequest {

	private static final long serialVersionUID = 4462868491415777411L;
	
	private String accessToken;
	
	public VerifyTokenRequest() {
		
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
	
	@NotEmpty
	public String getApplicationId() {
		return super.getApplicationId();
	}
	
	public void setApplicationId(String applicationId) {
		super.setApplicationId(applicationId);
	}
	
	public VerifyTokenRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
	
	public VerifyTokenRequest withApiEndpoint(String apiEndpoint) {
		setApiEndpoint(apiEndpoint);
		return this;
	}

	public VerifyTokenRequest withApiKeyId(String apiKeyId) {
		setApiKeyId(apiKeyId);
		return this;
	}
	
	public VerifyTokenRequest withApiKeySecret(String apiKeySecret) {
		setApiKeySecret(apiKeySecret);
		return this;
	}
	
	public VerifyTokenRequest withApplicationId(String applicationId) {
		setApplicationId(applicationId);
		return this;
	}
}