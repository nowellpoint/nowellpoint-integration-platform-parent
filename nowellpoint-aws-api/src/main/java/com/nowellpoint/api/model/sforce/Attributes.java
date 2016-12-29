package com.nowellpoint.api.model.sforce;

import java.io.Serializable;

public class Attributes implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8275564394346542878L;

	private String type;
	
	private String url;
	
	public Attributes() {
		
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