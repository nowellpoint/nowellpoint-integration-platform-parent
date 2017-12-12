package com.nowellpoint.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationInfo {
	
	private String id;
	private String number;
	private String domain;
	private Meta meta;
	
	public OrganizationInfo() {
		
	}

	public String getId() {
		return id;
	}

	public String getNumber() {
		return number;
	}

	public String getDomain() {
		return domain;
	}
	
	public Meta getMeta() {
		return meta;
	}
}