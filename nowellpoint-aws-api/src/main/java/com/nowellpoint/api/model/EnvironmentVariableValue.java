package com.nowellpoint.api.model;

public class EnvironmentVariableValue {
	
	private String name;
	
	private String value;
	
	public EnvironmentVariableValue() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}