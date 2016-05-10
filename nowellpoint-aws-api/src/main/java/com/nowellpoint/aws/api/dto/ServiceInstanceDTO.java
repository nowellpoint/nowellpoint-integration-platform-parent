package com.nowellpoint.aws.api.dto;

import java.io.Serializable;

public class ServiceInstanceDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String name;
	
	private String status;
	
	private String defaultEnvironment;

	public ServiceInstanceDTO() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDefaultEnvironment() {
		return defaultEnvironment;
	}

	public void setDefaultEnvironment(String defaultEnvironment) {
		this.defaultEnvironment = defaultEnvironment;
	}
}