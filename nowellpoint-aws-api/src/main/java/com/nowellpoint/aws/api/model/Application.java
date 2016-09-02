package com.nowellpoint.aws.api.model;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.api.codec.ApplicationCodec;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.MongoDocument;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="applications", codec=ApplicationCodec.class)
public class Application extends MongoDocument implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1315510190045597737L;
	
	private User owner;
	
	private String name;
	
	private String description;
	
	private Set<ServiceInstance> serviceInstances;
	
	private Set<Environment> environments;
	
	private Set<BatchJob> batchJobs;
	
	private String status;
	
	public Application() {
		
	}
	
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
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

	public Set<ServiceInstance> getServiceInstances() {
		return serviceInstances;
	}

	public void setServiceInstances(Set<ServiceInstance> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}

	public Set<Environment> getEnvironments() {
		return environments;
	}

	public void setEnvironments(Set<Environment> environments) {
		this.environments = environments;
	}

	public Set<BatchJob> getBatchJobs() {
		return batchJobs;
	}

	public void setBatchJobs(Set<BatchJob> batchJobs) {
		this.batchJobs = batchJobs;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}