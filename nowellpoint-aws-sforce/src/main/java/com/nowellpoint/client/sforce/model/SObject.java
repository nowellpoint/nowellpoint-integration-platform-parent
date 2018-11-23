package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SObject {
	
	@JsonProperty("attributes")
	private Attributes attributes;
	
	@JsonProperty("Id")
	private String id;
	
	public SObject() {
		
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public String getId() {
		return id;
	}
}