package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
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
	
	@Getter @JsonProperty("Name") private String name;
	@Getter @JsonProperty("IsActive") private Boolean isActive;
	@Getter @JsonProperty("SobjectType") private String sobjectType;
}