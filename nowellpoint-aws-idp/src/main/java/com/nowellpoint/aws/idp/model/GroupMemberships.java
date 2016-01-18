package com.nowellpoint.aws.idp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupMemberships implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4028402953654987070L;
	
	@JsonProperty(value="href")
	private String href;
	
	public GroupMemberships() {
		
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}