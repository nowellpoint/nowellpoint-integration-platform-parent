package com.nowellpoint.api.model.document;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.api.model.codec.ApplicationCodec;
import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.document.MongoDocument;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="applications", codec=ApplicationCodec.class)
public class Application extends MongoDocument implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1315510190045597737L;
	
	private UserRef owner;
	
	private String name;
	
	private String description;
	
	private Set<Instance> instances;
	
	private String status;
	
	public Application() {
		
	}
	
	public UserRef getOwner() {
		return owner;
	}

	public void setOwner(UserRef owner) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}