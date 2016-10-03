package com.nowellpoint.client.model;

public class GetAccountProfileRequest {

	private String id;
	
	public GetAccountProfileRequest() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public GetAccountProfileRequest withId(String id) {
		setId(id);
		return this;
	}
}