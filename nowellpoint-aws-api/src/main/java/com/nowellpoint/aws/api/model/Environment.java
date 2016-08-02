package com.nowellpoint.aws.api.model;

import java.util.Set;

public class Environment {
	
	private Integer index;
	
	private String name;
	
	private String label;
	
	private Boolean active;
	
	private Boolean locked;
	
	private String organization;
	
	private String endpoint;
	
	private Boolean test;
	
	private String testMessage;
	
	private Set<EnvironmentVariable> environmentVariables;
	
	public Environment() {

	}
	
	public Environment(String name, Boolean active) {
		super();
		setName(name);
		setActive(active);
	}
	
	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
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

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
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

	public Set<EnvironmentVariable> getEnvironmentVariables() {
		return environmentVariables;
	}

	public void setEnvironmentVariables(Set<EnvironmentVariable> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}
}