package com.nowellpoint.client.model;

public class DeleteScheduledJobRequest {
	
	private String id;
	
	public DeleteScheduledJobRequest() {
		
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public DeleteScheduledJobRequest withId(String id) {
		setId(id);
		return this;
	}
}