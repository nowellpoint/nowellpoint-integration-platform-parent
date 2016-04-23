package com.nowellpoint.www.app.model;

import java.util.HashSet;
import java.util.Set;

public class Environment {
	
	private String name;
	
	private Boolean active;
	
	private Set<EnvironmentVariable> environmentVariables;
	
	public Environment() {
		environmentVariables = new HashSet<EnvironmentVariable>();
	}
	
	public Environment(String name, Boolean active) {
		super();
		setName(name);
		setActive(active);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Set<EnvironmentVariable> getEnvironmentVariables() {
		return environmentVariables;
	}

	public void setEnvironmentVariables(Set<EnvironmentVariable> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}
}