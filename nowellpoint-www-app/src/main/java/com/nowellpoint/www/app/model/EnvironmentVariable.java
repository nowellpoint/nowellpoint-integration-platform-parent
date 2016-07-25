package com.nowellpoint.www.app.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class EnvironmentVariable {
	
	private String variable;
	
	private String value;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private Boolean locked;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private Boolean encrypted;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private List<EnvironmentVariableValue> environmentVariableValues;
	
	public EnvironmentVariable() {
		setLocked(Boolean.FALSE);
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}
	
	public Boolean getEncrypted() {
		return encrypted;
	}

	public void setEncrypted(Boolean encrypted) {
		this.encrypted = encrypted;
	}

	public List<EnvironmentVariableValue> getEnvironmentVariableValues() {
		if (environmentVariableValues == null) {
			setEnvironmentVariableValues(new ArrayList<EnvironmentVariableValue>());
		}
		return environmentVariableValues;
	}

	public void setEnvironmentVariableValues(List<EnvironmentVariableValue> environmentVariableValues) {
		this.environmentVariableValues = environmentVariableValues;
	}
}