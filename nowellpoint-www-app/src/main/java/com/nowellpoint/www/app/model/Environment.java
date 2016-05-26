package com.nowellpoint.www.app.model;

import java.util.ArrayList;
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
	
	private Integer index;
	
	private String name;
	
	private String label;
	
	private Boolean active;
	
	private String organization;
	
	private String endpoint;
	
	private String status;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private Boolean locked;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private List<EnvironmentVariable> environmentVariables;
	
	private Map<String, List<EnvironmentVariableValue>> environmentVariableValues;
	
	public Environment() {
		setLocked(Boolean.FALSE);
		setActive(Boolean.FALSE);
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
}