package com.nowellpoint.api.model.dto;

public class Project extends AbstractResource {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4568819604209454827L;

	private String name;
	
	private String description;
	
	private String stage;
	
	private AccountProfile owner;
	
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

	public AccountProfile getOwner() {
		return owner;
	}

	public void setOwner(AccountProfile owner) {
		this.owner = owner;
	}
}