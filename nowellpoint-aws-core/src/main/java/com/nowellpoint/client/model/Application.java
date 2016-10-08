package com.nowellpoint.client.model;

import java.util.Collections;
import java.util.List;

public class Application extends AbstractResource {
	
	private AccountProfile owner;
	
	private String name;
	
	private String description;
	
	private List<ServiceInstance> serviceInstances;
	
	private List<Environment> environments;
	
	private String status;
	
	public Application() {
		setServiceInstances(Collections.emptyList());
		setEnvironments(Collections.emptyList());
	}
	
	public Application(String id) {
		setId(id);
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public AccountProfile getOwner() {
		return owner;
	}

	public void setOwner(AccountProfile owner) {
		this.owner = owner;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ServiceInstance> getServiceInstances() {
		return serviceInstances;
	}

	public void setServiceInstances(List<ServiceInstance> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}

	public List<Environment> getEnvironments() {
		return environments;
	}

	public void setEnvironments(List<Environment> environments) {
		this.environments = environments;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}