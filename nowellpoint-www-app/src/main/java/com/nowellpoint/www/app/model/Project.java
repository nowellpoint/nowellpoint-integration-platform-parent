package com.nowellpoint.www.app.model;

public class Project extends BaseEntity {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4568819604209454827L;

	private String name;
	
	private String description;
	
	private String stage;
	
	private Identity owner;
	
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

	public Identity getOwner() {
		return owner;
	}

	public void setOwner(Identity owner) {
		this.owner = owner;
	}
}