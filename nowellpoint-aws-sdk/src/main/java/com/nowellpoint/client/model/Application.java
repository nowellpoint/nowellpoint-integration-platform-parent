package com.nowellpoint.client.model;

import java.util.Collections;
import java.util.List;

public class Application extends AbstractResource {
	
	private AccountProfile owner;
	
	private String name;
	
	private String description;
	
	private List<Instance> instances;
	
	private String status;
	
	public Application() {
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

	public List<Instance> getEnvironments() {
		return instances;
	}

	public void setEnvironments(List<Instance> instances) {
		this.instances = instances;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}