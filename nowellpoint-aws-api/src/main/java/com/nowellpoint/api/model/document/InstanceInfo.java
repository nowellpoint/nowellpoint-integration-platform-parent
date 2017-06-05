package com.nowellpoint.api.model.document;

import java.io.Serializable;

public class InstanceInfo implements Serializable {

	private static final long serialVersionUID = 6831881100300944627L;

	private String key;
	
	private String name;
	
	private String serviceEndpoint;
	
	private String apiVersion;
	
	private Boolean isSandbox;
	
	public InstanceInfo() {
		
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(String serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public Boolean getIsSandbox() {
		return isSandbox;
	}

	public void setIsSandbox(Boolean isSandbox) {
		this.isSandbox = isSandbox;
	}
}