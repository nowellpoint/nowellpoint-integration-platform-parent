package com.nowellpoint.client.sforce.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SObject implements Serializable {
	
	private static final long serialVersionUID = 8436267729392469449L;

	@JsonProperty("attributes")
	private Attributes attributes;
	
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("CreatedById")
	private String createdById;
	
	@JsonProperty("CreatedDate")
	private Date createdDate;
	
	@JsonProperty("LastModifiedById")
	private String lastModifiedById;
	
	@JsonProperty("LastModifiedDate")
	private Date lastModifiedDate;
	
	public SObject() {
		
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public String getId() {
		return id;
	}

	public String getCreatedById() {
		return createdById;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getLastModifiedById() {
		return lastModifiedById;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
}