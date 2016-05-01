package com.nowellpoint.www.app.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Environment {
	
	private Integer index;
	
	private String name;
	
	private String label;
	
	private Boolean active;
	
	private Boolean locked;
	
	private List<EnvironmentVariable> environmentVariables;
	
	private Map<String, List<EnvironmentVariableValue>> environmentVariableValues;
	
	public Environment() {
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