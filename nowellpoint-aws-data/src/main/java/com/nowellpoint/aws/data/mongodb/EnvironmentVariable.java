package com.nowellpoint.aws.data.mongodb;

public class EnvironmentVariable {
	
	private String variable;
	
	private String value;
	
	private Boolean locked;
	
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
}