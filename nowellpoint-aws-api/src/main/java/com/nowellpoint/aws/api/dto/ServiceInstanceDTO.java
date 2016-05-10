package com.nowellpoint.aws.api.dto;

public class ServiceInstanceDTO {
	
	private String name;
	
	private String defaultEnvironment;

	public ServiceInstanceDTO() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultEnvironment() {
		return defaultEnvironment;
	}

	public void setDefaultEnvironment(String defaultEnvironment) {
		this.defaultEnvironment = defaultEnvironment;
	}
}