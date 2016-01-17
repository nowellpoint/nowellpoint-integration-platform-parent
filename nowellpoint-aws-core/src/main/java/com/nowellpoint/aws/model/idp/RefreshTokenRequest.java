package com.nowellpoint.aws.model.idp;

import org.hibernate.validator.constraints.NotEmpty;

public class RefreshTokenRequest extends AbstractIdpRequest {
	
	private static final long serialVersionUID = -3186641818593860386L;
	
	private String refreshToken;
	
	public RefreshTokenRequest() {
		
	}
	
	@NotEmpty
	public String getRefreshToken() {
		return refreshToken;
	}
	
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
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
	
	public RefreshTokenRequest withApiEndpoint(String apiEndpoint) {
		setApiEndpoint(apiEndpoint);
		return this;
	}
	
	public RefreshTokenRequest withApiKeyId(String apiKeyId) {
		setApiKeyId(apiKeyId);
		return this;
	}
	
	public RefreshTokenRequest withApiKeySecret(String apiKeySecret) {
		setApiKeySecret(apiKeySecret);
		return this;
	}
	
	public RefreshTokenRequest withRefreshToken(String refreshToken) {
		setRefreshToken(refreshToken);
		return this;
	}
	
	public RefreshTokenRequest withApplicationId(String applicationId) {
		setApplicationId(applicationId);
		return this;
	}
}