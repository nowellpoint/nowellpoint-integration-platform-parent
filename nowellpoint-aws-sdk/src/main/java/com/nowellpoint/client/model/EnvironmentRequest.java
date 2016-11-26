package com.nowellpoint.client.model;

public class EnvironmentRequest {
	
	private String environmentName;
	
	private Boolean isActive;
	
	private String authEndpoint;
	
	private String apiVersion;
	
	private String username;
	
	private String password;
	
	private String securityToken;
	
	public EnvironmentRequest() {

	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getAuthEndpoint() {
		return authEndpoint;
	}

	public void setAuthEndpoint(String authEndpoint) {
		this.authEndpoint = authEndpoint;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
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

	public String getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}
	
	public EnvironmentRequest withEnvironmentName(String environmentName) {
		setEnvironmentName(environmentName);
		return this;
	}
	
	public EnvironmentRequest withIsActive(Boolean isActive) {
		setIsActive(isActive);
		return this;
	}
	
	public EnvironmentRequest withAuthEndpoint(String authEndpoint) {
		setAuthEndpoint(authEndpoint);
		return this;
	}
	
	public EnvironmentRequest withApiVersion(String apiVersion) {
		setApiVersion(apiVersion);
		return this;
	}
	
	public EnvironmentRequest withUsername(String username) {
		setUsername(username);
		return this;
	}
	
	public EnvironmentRequest withPassword(String password) {
		setPassword(password);
		return this;
	}
	
	public EnvironmentRequest withSecurityToken(String securityToken) {
		setSecurityToken(securityToken);
		return this;
	}
}