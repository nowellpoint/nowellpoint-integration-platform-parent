package com.nowellpoint.aws.api.dto;

public class ProjectDTO extends AbstractDTO {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4568819604209454827L;

	private String name;
	
	private String description;
	
	private String stage;
	
	private String ownerId;
	
	public ProjectDTO() {
		
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

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}