package com.nowellpoint.www.app.model;

public class Project extends Resource {

	private String name;
	
	private String description;
	
	private String stage;
	
	private AccountProfile owner;
	
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

	public AccountProfile getOwner() {
		return owner;
	}

	public void setOwner(AccountProfile owner) {
		this.owner = owner;
	}
}