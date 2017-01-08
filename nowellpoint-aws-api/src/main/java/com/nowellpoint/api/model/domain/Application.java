package com.nowellpoint.api.model.domain;

import java.util.HashSet;
import java.util.Set;

public class Application extends AbstractResource {
	
	private AccountProfile owner;
	
	private String name;
	
	private String description;
	
	private Set<Instance> instances;
	
	private String status;
	
	public Application() {
		
	}

	public AccountProfile getOwner() {
		return owner;
	}

	public void setOwner(AccountProfile owner) {
		this.owner = owner;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Instance> getEnvironments() {
		return instances;
	}

	public void setEnvironments(Set<Instance> instances) {
		this.instances = instances;
	}
	
	public void addEnvironment(Instance instance) {
		if (instances == null || instances.isEmpty()) {
			instances = new HashSet<Instance>();
		}
		instances.add(instance);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}