package com.nowellpoint.www.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@JsonInclude(Include.NON_EMPTY)
public class Environment {
	
	private String key;
	
	private Date addedOn;
	
	private Date updatedOn;
	
	private String name;
	
	private String label;
	
	private Boolean active;
	
	private String organization;
	
	private String serviceEndpoint;
	
	private String authEndpoint;
	
	private String username;
	
	private String password;
	
	private String securityToken;
	
	private Boolean test;
	
	private String testMessage;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private Boolean locked;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private List<EnvironmentVariable> environmentVariables;
	
	private Map<String, List<EnvironmentVariableValue>> environmentVariableValues;
	
	public Environment() {
		setLocked(Boolean.FALSE);
		setActive(Boolean.FALSE);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
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

	public Boolean getTest() {
		return test;
	}

	public void setTest(Boolean test) {
		this.test = test;
	}

	public String getTestMessage() {
		return testMessage;
	}

	public void setTestMessage(String testMessage) {
		this.testMessage = testMessage;
	}
	
	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
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
	
	public Environment withName(String name) {
		setName(name);
		return this;
	}
	
	public Environment withLabel(String label) {
		setLabel(label);
		return this;
	}
	
	public Environment withActive(Boolean active) {
		setActive(active);
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