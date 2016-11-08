package com.nowellpoint.api.dto.idp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Application implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2456321938436032586L;
	
	@JsonProperty(value="href")
	private String href;
	
	public Application() {
		
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}