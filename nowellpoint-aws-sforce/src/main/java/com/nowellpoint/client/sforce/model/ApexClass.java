package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApexClass extends SObject {
	
	private static final long serialVersionUID = -6882084692685797426L;

	public static final String QUERY = "Select "
			+ "ApiVersion, "
			+ "Body, "
			+ "BodyCrc, "
			+ "CreatedById, "
			+ "CreatedDate, "
			+ "Id, "
			+ "IsValid, "
			+ "LastModifiedById, "
			+ "LastModifiedDate, "
			+ "LengthWithoutComments, "
			+ "Name, "
			+ "NamespacePrefix, "
			+ "Status "
			+ "From ApexClass";
	
	@Getter @JsonProperty("Body") private String body;
	@Getter @JsonProperty("IsValid") private Boolean isValid;
	@Getter @JsonProperty("ApiVersion") private Double apiVersion;
	@Getter @JsonProperty("Name") private String name;
	
	public ApexClass() {
		
	}
}