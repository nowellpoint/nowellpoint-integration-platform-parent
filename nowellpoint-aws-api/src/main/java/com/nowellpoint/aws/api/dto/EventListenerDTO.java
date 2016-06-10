package com.nowellpoint.aws.api.dto;

public class EventListenerDTO {
	
	private String name;
	
	private String label;
	
	private Boolean triggerable;
	
	private Boolean searchable;
	
	private Boolean create;
	
	private Boolean update;
	
	private Boolean delete;
	
	public EventListenerDTO() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getTriggerable() {
		return triggerable;
	}

	public void setTriggerable(Boolean triggerable) {
		this.triggerable = triggerable;
	}

	public Boolean getSearchable() {
		return searchable;
	}

	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
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