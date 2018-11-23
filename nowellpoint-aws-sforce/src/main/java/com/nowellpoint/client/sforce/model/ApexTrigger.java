package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApexTrigger {
	private Attributes attributes;
	
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("Body")
	private String body;
	
	@JsonProperty("IsValid")
	private Boolean isValid;
	
	@JsonProperty("ApiVersion")
	private Double apiVersion;
	
	@JsonProperty("Name")
	private String name;
	
	//BodyCrc, CreatedById, CreatedDate, LastModifiedById, LastModifiedDate, LengthWithoutComments, Name, NamespacePrefix, Status, SystemModstamp
	
	public ApexTrigger() {
		
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

	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}