package com.nowellpoint.aws.api.dto;

public class EventListenerDTO {
	
	private String type;
	
	private Boolean create;
	
	private Boolean update;
	
	private Boolean delete;
	
	public EventListenerDTO() {
		
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public Boolean getCreate() {
		return create;
	}

	public void setCreate(Boolean create) {
		this.create = create;
	}

	public Boolean getUpdate() {
		return update;
	}

	public void setUpdate(Boolean update) {
		this.update = update;
	}

	public Boolean getDelete() {
		return delete;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;
	}
}