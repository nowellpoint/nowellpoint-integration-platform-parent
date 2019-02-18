package com.nowellpoint.sdk.salesforce.model.sobject;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NamedLayoutInfo implements Serializable {

	private static final long serialVersionUID = -7984076664176001329L;
	
	private String name;
	
	public NamedLayoutInfo() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}