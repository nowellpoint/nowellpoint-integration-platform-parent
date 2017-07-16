package com.nowellpoint.api.model.document;

import java.io.Serializable;

public class ReferenceLink implements Serializable {

	private static final long serialVersionUID = 2268682977974123822L;

	private String id;
	
	private String name;
	
	public ReferenceLink() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}