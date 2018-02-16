package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApexClass {
	private Attributes attributes;
	
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("Body")
	private String body;
	
	public ApexClass() {
		
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public String getId() {
		return id;
	}

	public String getBody() {
		return body;
	}
}