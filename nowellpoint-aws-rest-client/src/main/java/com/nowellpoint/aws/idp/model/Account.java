package com.nowellpoint.aws.idp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 4359339056490011294L;
	
	@JsonProperty(value="href")
	private String href;
	
	public Account() {
		
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}