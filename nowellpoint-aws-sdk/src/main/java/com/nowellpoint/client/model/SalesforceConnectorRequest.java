package com.nowellpoint.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesforceConnectorRequest {

	/**
	 * 
	 */

	private String name;

	/**
	 * 
	 */
	
	private String tag;
	
	public SalesforceConnectorRequest() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public SalesforceConnectorRequest withName(String name) {
		setName(name);
		return this;
	}
	
	public SalesforceConnectorRequest withTag(String tag) {
		setTag(tag);
		return this;
	}
}