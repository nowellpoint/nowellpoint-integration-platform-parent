package com.nowellpoint.www.app.model;

import java.util.List;

public class Project extends BaseEntity {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4568819604209454827L;

	private String name;
	
	private String description;
	
	private String stage;
	
	private String owner;
	
	private List<Application> salesforceOrganizations;
	
	public Project() {
		
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

	public void setSalesforceOrganizations(List<Application> salesforceOrganizations) {
		this.salesforceOrganizations = salesforceOrganizations;
	}
}