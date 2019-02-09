package com.nowellpoint.client.sforce.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SObject implements Serializable {
	
	private static final long serialVersionUID = 8436267729392469449L;
	
	protected static final String SOBJECT_QUERY = "Select "
			+ "Id, "
			+ "Name, "
			+ "CreatedById, "
			+ "CreatedDate, "
			+ "LastModifiedById, "
			+ "LastModifiedDate ";

	@Getter @JsonProperty("attributes") private Attributes attributes;
	@Getter @JsonProperty("Id") private String id;
	@Getter @JsonProperty("Name") private String name;
	@Getter @JsonProperty("CreatedById") private String createdById;
	@Getter @JsonProperty("CreatedDate") private Date createdDate;
	@Getter @JsonProperty("LastModifiedById") private String lastModifiedById;
	@Getter @JsonProperty("LastModifiedDate") private Date lastModifiedDate;
	
	public SObject() { }
}