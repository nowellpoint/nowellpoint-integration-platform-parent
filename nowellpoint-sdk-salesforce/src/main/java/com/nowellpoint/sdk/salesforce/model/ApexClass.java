package com.nowellpoint.sdk.salesforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
			+ "Name, NamespacePrefix, "
			+ "Status "
			+ "From ApexClass";
	
	@JsonProperty("Body")
	private String body;
	
	@JsonProperty("IsValid")
	private Boolean isValid;
	
	@JsonProperty("ApiVersion")
	private Double apiVersion;
	
	@JsonProperty("Name")
	private String name;
	
	public ApexClass() {
		
	}

	public String getBody() {
		return body;
	}

	public Boolean getIsValid() {
		return isValid;
	}

	public String getName() {
		return name;
	}
}