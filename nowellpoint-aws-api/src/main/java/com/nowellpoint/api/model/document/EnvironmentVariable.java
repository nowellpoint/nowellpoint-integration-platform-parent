package com.nowellpoint.api.model.document;

import java.util.Set;

public class EnvironmentVariable {
	
	private String variable;
	
	private String value;
	
	private Boolean locked;
	
	private Boolean encrypted;
	
	private Set<EnvironmentVariableValue> environmentVariableValues;
	
	public EnvironmentVariable() {
		
	}
	
	public EnvironmentVariable(String variable, String value) {
		setVariable(variable);
		setValue(value);
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
	
	public Set<EnvironmentVariableValue> getEnvironmentVariableValues() {
		return environmentVariableValues;
	}

	public void setEnvironmentVariableValues(Set<EnvironmentVariableValue> environmentVariableValues) {
		this.environmentVariableValues = environmentVariableValues;
	}
}