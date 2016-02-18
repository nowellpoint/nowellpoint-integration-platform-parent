package com.nowellpoint.www.app.model.sforce;

import java.io.Serializable;

public class Attributes implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4180438562996624005L;
	
	private String id;

	private String type;
	
	private String url;
	
	public Attributes() {
		
	}
	
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}