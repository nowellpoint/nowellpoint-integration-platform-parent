package com.nowellpoint.api.model.domain;

public class Project extends AbstractResource {

	private String name;
	
	private String description;
	
	private String stage;
	
	private AccountProfile owner;
	
	public Project() {
	}
	
	public Project(String id) {
		super(id);
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

	public AccountProfile getOwner() {
		return owner;
	}

	public void setOwner(AccountProfile owner) {
		this.owner = owner;
	}
}