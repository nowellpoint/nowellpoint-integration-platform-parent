package com.nowellpoint.aws.model;

import java.io.Serializable;
import java.util.Date;

public class Project implements Serializable {

	private static final long serialVersionUID = 2884164327750192485L;
	
	private String id;
	
	private String name;
	
	private String description;
	
	private String stage;
	
	private Date creationDate;
	
	private Date lastModifiedDate;
	
	private String owner;
	
	private String createdBy;
	
	private String lastModifiedBy;

	public Project() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	
	public Project id(String id) {
		setId(id);
		return this;
	}
	
	public Project name(String name) {
		setName(name);
		return this;
	}
	
	public Project description(String description) {
		setDescription(description);
		return this;
	}
	
	public Project stage(String stage) {
		setStage(stage);
		return this;
	}
	
	public Project creationDate(Date creationDate) {
		setCreationDate(creationDate);
		return this;
	}
	
	public Project lastModifiedDate(Date lastModifiedDate) {
		setLastModifiedDate(lastModifiedDate);
		return this;
	}
	
	public Project owner(String owner) {
		setOwner(owner);
		return this;
	}
	
	public Project createdBy(String createdBy) {
		setCreatedBy(createdBy);
		return this;
	}
	
	public Project lastModifiedBy(String lastModifiedBy) {
		setLastModifiedBy(lastModifiedBy);
		return this;
	}
}