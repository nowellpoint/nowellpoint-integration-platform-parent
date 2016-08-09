package com.nowellpoint.www.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class Environment {
	
	private String key;
	
	private Date addedOn;
	
	private Date updatedOn;
	
	private String environmentName;
	
	private Boolean isActive;
	
	private String organizationId;
	
	private String organizationName;
	
	private String serviceEndpoint;
	
	private String authEndpoint;
	
	private String apiVersion;
	
	private String userId;
	
	private String username;
	
	private String password;
	
	private String securityToken;
	
	private Boolean isSandbox;
	
	private Boolean isValid;
	
	private String testMessage;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private Boolean isReadOnly;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private List<EnvironmentVariable> environmentVariables;
	
	private Map<String, List<EnvironmentVariableValue>> environmentVariableValues;
	
	public Environment() {
		setIsReadOnly(Boolean.FALSE);
		setIsActive(Boolean.FALSE);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getAddedOn() {
		return addedOn;
	}

	public void setAddedOn(Date addedOn) {
		this.addedOn = addedOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
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

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(String serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public Boolean getIsSandbox() {
		return isSandbox;
	}

	public void setIsSandbox(Boolean isSandbox) {
		this.isSandbox = isSandbox;
	}

	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	public String getTestMessage() {
		return testMessage;
	}

	public void setTestMessage(String testMessage) {
		this.testMessage = testMessage;
	}
	
	public Boolean getIsReadOnly() {
		return isReadOnly;
	}

	public void setIsReadOnly(Boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public List<EnvironmentVariable> getEnvironmentVariables() {
		if (environmentVariables == null) {
			setEnvironmentVariables(new ArrayList<EnvironmentVariable>());
		} else {
			setEnvironmentVariables(environmentVariables
					.stream()
					.sorted((p1, p2) -> p1.getVariable().compareTo(p2.getVariable()))
					.collect(Collectors.toList()));
		}
		return environmentVariables;
	}

	public void setEnvironmentVariables(List<EnvironmentVariable> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}
	
	public Map<String,List<EnvironmentVariableValue>> getEnvironmentVariableValues() {
		if (environmentVariableValues == null) {
			setEnvironmentVariableValues(new HashMap<String, List<EnvironmentVariableValue>>());
		}
		return environmentVariableValues;
	}

	public void setEnvironmentVariableValues(Map<String, List<EnvironmentVariableValue>> environmentVariableValues) {
		this.environmentVariableValues = environmentVariableValues;
	}
	
	public Environment withEnvironmentName(String environmentName) {
		setEnvironmentName(environmentName);
		return this;
	}
	
	public Environment withIsActive(Boolean active) {
		setIsActive(active);
		return this;
	}
	
	public Environment withAuthEndpoint(String authEndpoint) {
		setAuthEndpoint(authEndpoint);
		return this;
	}
	
	public Environment withUsername(String username) {
		setUsername(username);
		return this;
	}
	
	public Environment withPassword(String password) {
		setPassword(password);
		return this;
	}
	
	public Environment withSecurityToken(String securityToken) {
		setSecurityToken(securityToken);
		return this;
	}
}