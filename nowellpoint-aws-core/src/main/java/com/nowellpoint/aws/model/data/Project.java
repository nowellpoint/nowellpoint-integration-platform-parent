package com.nowellpoint.aws.model.data;

public class Project extends AbstractDocument {

	private static final long serialVersionUID = 2884164327750192485L;
	
	private String name;
	
	private String description;
	
	private String stage;
	
	private String owner;

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
	
	public Project withId(String id) {
		setId(id);
		return this;
	}
	
	public Project withName(String name) {
		setName(name);
		return this;
	}
	
	public Project withDescription(String description) {
		setDescription(description);
		return this;
	}
	
	public Project withStage(String stage) {
		setStage(stage);
		return this;
	}
}