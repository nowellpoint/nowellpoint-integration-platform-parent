package com.nowellpoint.aws.data.mongodb;

import java.io.Serializable;

public class Service implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1537820218475752902L;
	
	private String name;
	
	private String description;
	
	public Service() {
		
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

}