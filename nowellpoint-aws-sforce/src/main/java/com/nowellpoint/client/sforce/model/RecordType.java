package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordType extends SObject {
	
	private static final long serialVersionUID = -5919993493822022243L;

	public static final String QUERY = "Select "
			+ "BusinessProcessId, "
			+ "CreatedById, "
			+ "CreatedDate, "
			+ "Description, "
			+ "DeveloperName, "
			+ "Id, "
			+ "IsActive, "
			+ "LastModifiedById, "
			+ "LastModifiedDate, "
			+ "Name, "
			+ "NamespacePrefix, "
			+ "SobjectType "
			+ "From RecordType";
	
	@JsonProperty("Name")
	private String name;
	
	@JsonProperty("IsActive")
	private Boolean isActive;
	
	@JsonProperty("SobjectType")
	private String sobjectType;
	
	public RecordType() {
		
	}

	public String getName() {
		return name;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public String getSobjectType() {
		return sobjectType;
	}
}