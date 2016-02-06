package com.nowellpoint.aws.model.data;

import java.util.List;

public class Project extends AbstractDocument {

	private static final long serialVersionUID = 2884164327750192485L;
	
	private String name;
	
	private String description;
	
	private String stage;
	
	private String owner;
	
	private List<SalesforceOrganization> salesforceOrganizations;

	public Project() {
		
	}
	
	public Project(String id) {
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
	
	public List<SalesforceOrganization> getSalesforceOrganizations() {
		return salesforceOrganizations;
	}
	
	public void setSalesforceOrganizations(List<SalesforceOrganization>salesforceOrganizations) {
		this.salesforceOrganizations = salesforceOrganizations;
	}
}