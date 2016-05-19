package com.nowellpoint.sforce.model;

public class Notification {
	
	private String id;
	
	private Sobject sobject;
	
	public Notification() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Sobject getSobject() {
		return sobject;
	}

	public void setSobject(Sobject sobject) {
		this.sobject = sobject;
	}
}