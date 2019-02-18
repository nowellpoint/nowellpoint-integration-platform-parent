package com.nowellpoint.sdk.salesforce.model.sobject;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SupportedScope implements Serializable {

	private static final long serialVersionUID = -1537380964305421895L;
	
	private String label;
	
	private String name;
	
	public SupportedScope() {
		
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}