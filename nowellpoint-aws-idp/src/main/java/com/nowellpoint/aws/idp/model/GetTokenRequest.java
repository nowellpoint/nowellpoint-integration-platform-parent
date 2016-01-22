package com.nowellpoint.aws.idp.model;

import org.hibernate.validator.constraints.NotEmpty;

public class GetTokenRequest extends AbstractIdpRequest {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5687820017409617494L;
	
	private String applicationId;
	
	private String username;
	
	private String password;

	public GetTokenRequest() {
		
	}
	
	@NotEmpty
	public String getApiEndpoint() {
		return super.getApiEndpoint();
	}
	
	public void setApiEndpoint(String apiEndpoint) {
		super.setApiEndpoint(apiEndpoint);
	}
	
	@NotEmpty
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@NotEmpty
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@NotEmpty
	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	
	public GetTokenRequest withUsername(String username) {
		setUsername(username);
		return this;
	}
	
	public GetTokenRequest withPassword(String password) {
		setPassword(password);
		return this;
	}
	
	public GetTokenRequest withApiEndpoint(String apiEndpoint) {
		setApiEndpoint(apiEndpoint);
		return this;
	}
	
	public GetTokenRequest withApplicationId(String applicationId) {
		setApplicationId(applicationId);
		return this;
	}
	
	public GetTokenRequest withApiKeyId(String apiKeyId) {
		setApiKeyId(apiKeyId);
		return this;
	}
	
	public GetTokenRequest withApiKeySecret(String apiKeySecret) {
		setApiKeySecret(apiKeySecret);
		return this;
	}
}