package com.nowellpoint.api.model.document;

import java.util.List;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.api.model.codec.ProjectCodec;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.MongoDocument;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="projects", codec=ProjectCodec.class)
public class ProjectDocument extends MongoDocument {

	private static final long serialVersionUID = 2884164327750192485L;
	
	private String name;
	
	private String description;
	
	private String stage;
	
	private User owner;
	
	private List<ApplicationDocument> salesforceOrganizations;

	public ProjectDocument() {
		
	}
	
	public ProjectDocument(ObjectId id) {
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

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	public List<ApplicationDocument> getSalesforceOrganizations() {
		return salesforceOrganizations;
	}
	
	public void setSalesforceOrganizations(List<ApplicationDocument>salesforceOrganizations) {
		this.salesforceOrganizations = salesforceOrganizations;
	}
}