package com.nowellpoint.client.model;

public class ServiceRequest {
	
	private String id;
	
	private String serviceId;
	
	public ServiceRequest() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public ServiceRequest withId(String id) {
		setId(id);
		return this;
	}
	
	public ServiceRequest withServiceId(String serviceId) {
		setServiceId(serviceId);
		return this;
	}
}