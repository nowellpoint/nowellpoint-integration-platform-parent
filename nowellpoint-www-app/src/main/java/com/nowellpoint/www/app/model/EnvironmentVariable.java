package com.nowellpoint.www.app.model;

public class EnvironmentVariable {
	
	private String variable;
	
	private String value;
	
	private Boolean locked;
	
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
}