package com.nowellpoint.www.app.model;

public class EventListener {
	
	private String type;
	
	private Boolean create;
	
	private Boolean update;
	
	private Boolean delete;
	
	public EventListener() {
		
	}
	
	public EventListener(String type, Boolean create, Boolean update, Boolean delete) {
		this.type = type;
		this.create = create;
		this.update = update;
		this.delete = delete;
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