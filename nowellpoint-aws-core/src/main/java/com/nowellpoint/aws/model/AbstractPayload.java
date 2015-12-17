package com.nowellpoint.aws.model;

import java.io.Serializable;

public class AbstractPayload implements Serializable {
	
	private static final long serialVersionUID = 4644224121071606758L;
	
	private String id;
	
	public AbstractPayload() {
		
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
}