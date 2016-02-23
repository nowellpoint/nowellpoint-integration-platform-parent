package com.nowellpoint.aws.data.model;

import java.util.List;

import org.bson.types.ObjectId;

import com.nowellpoint.aws.data.annotation.Handler;

@Handler(queueName="MONGODB_PROJECT_COLLECTION_QUEUE", collectionName="projects")
public class Project extends AbstractDocument {

	private static final long serialVersionUID = 2884164327750192485L;
	
	private String name;
	
	private String description;
	
	private String stage;
	
	private String owner;
	
	private List<Application> salesforceOrganizations;

	public Project() {
		
	}
	
	public Project(ObjectId id) {
		setId(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	
	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public List<Application> getSalesforceOrganizations() {
		return salesforceOrganizations;
	}
	
	public void setSalesforceOrganizations(List<Application>salesforceOrganizations) {
		this.salesforceOrganizations = salesforceOrganizations;
	}
}