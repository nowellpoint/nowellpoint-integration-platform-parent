package com.nowellpoint.client.sforce.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DescribeSobjectResult {
	
	private Boolean activateable;
	
	private Boolean createable;
	
	private List<Field> fields;
	
	public DescribeSobjectResult() {
		
	}

	public Boolean getActivateable() {
		return activateable;
	}

	public void setActivateable(Boolean activateable) {
		this.activateable = activateable;
	}

	public Boolean getCreateable() {
		return createable;
	}

	public void setCreateable(Boolean createable) {
		this.createable = createable;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
}