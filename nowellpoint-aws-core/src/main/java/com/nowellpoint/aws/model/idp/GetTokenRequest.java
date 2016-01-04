package com.nowellpoint.aws.model.idp;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class GetTokenRequest extends AbstractLambdaRequest {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5687820017409617494L;
	
	private String username;
	
	private String password;
	
	private String endpoint;
	
	private String applicationId;
	
	private String apiKeyId;
	
	private String apiKeySecret;

	public GetTokenRequest() {
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getApiKeyId() {
		return apiKeyId;
	}

	public void setApiKeyId(String apiKeyId) {
		this.apiKeyId = apiKeyId;
	}

	public String getApiKeySecret() {
		return apiKeySecret;
	}

	public void setApiKeySecret(String apiKeySecret) {
		this.apiKeySecret = apiKeySecret;
	}

	public GetTokenRequest withUsername(String username) {
		setUsername(username);
		return this;
	}
	
	public GetTokenRequest withPassword(String password) {
		setPassword(password);
		return this;
	}
	
	public GetTokenRequest withEndpoint(String endpoint) {
		setEndpoint(endpoint);
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