package com.nowellpoint.www.app.model;

import java.util.HashSet;
import java.util.Set;

public class Environment {
	
	private Integer index;
	
	private String name;
	
	private String label;
	
	private Boolean active;
	
	private Boolean locked;
	
	private Set<EnvironmentVariable> environmentVariables;
	
	public Environment() {
		setEnvironmentVariables(new HashSet<EnvironmentVariable>());
		environmentVariables.add(new EnvironmentVariable());
		setLocked(Boolean.FALSE);
		setActive(Boolean.FALSE);
	}
	
	public Environment(String name, String label, Boolean active, Boolean locked) {
		super();
		setName(name);
		setLabel(label);
		setActive(active);
		setLocked(locked);
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
	
	public Set<EnvironmentVariable> getEnvironmentVariables() {
		return environmentVariables;
	}

	public void setEnvironmentVariables(Set<EnvironmentVariable> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}
}