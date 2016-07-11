package com.nowellpoint.aws.api.dto;

import java.util.Set;

import com.nowellpoint.aws.api.model.EnvironmentVariableValue;

public class EnvironmentVariableDTO {
	
	private String variable;
	
	private String value;
	
	private Boolean encrypted;
	
	private Set<EnvironmentVariableValue> environmentVariableValues;
	
	public EnvironmentVariableDTO() {
		
	}
	
	public EnvironmentVariableDTO(String variable, String value) {
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