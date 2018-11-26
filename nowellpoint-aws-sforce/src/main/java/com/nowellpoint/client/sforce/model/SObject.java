package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SObject implements Serializable {
	
	private static final long serialVersionUID = 8436267729392469449L;

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