package com.nowellpoint.aws.sforce.model;

import java.io.Serializable;

public class SObject implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = 4096111144430094383L;

	private String type;

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}