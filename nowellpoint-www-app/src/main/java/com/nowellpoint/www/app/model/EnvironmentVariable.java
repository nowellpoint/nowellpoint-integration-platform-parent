package com.nowellpoint.www.app.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EnvironmentVariable {
	
	private String variable;
	
	private String value;
	
	private Boolean locked;
	
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

	@JsonIgnore
	public List<EnvironmentVariableValue> getEnvironmentVariableValues() {
		if (environmentVariableValues == null) {
			setEnvironmentVariableValues(new ArrayList<EnvironmentVariableValue>());
		}
		return environmentVariableValues;
	}

	@JsonIgnore
	public void setEnvironmentVariableValues(List<EnvironmentVariableValue> environmentVariableValues) {
		this.environmentVariableValues = environmentVariableValues;
	}
}