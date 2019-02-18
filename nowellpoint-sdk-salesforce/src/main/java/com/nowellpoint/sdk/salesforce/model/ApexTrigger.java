package com.nowellpoint.sdk.salesforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApexTrigger extends SObject {
	
	private static final long serialVersionUID = -1705806933872559501L;

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
			+ "Status, "
			+ "TableEnumOrId, "
			+ "UsageAfterDelete, "
			+ "UsageAfterInsert, "
			+ "UsageAfterUndelete, "
			+ "UsageAfterUpdate, "
			+ "UsageBeforeDelete, "
			+ "UsageBeforeInsert, "
			+ "UsageBeforeUpdate, "
			+ "UsageIsBulk "
			+ "From ApexTrigger";
	
	@JsonProperty("Body")
	private String body;
	
	@JsonProperty("IsValid")
	private Boolean isValid;
	
	@JsonProperty("ApiVersion")
	private Double apiVersion;
	
	@JsonProperty("Name")
	private String name;
	
	public ApexTrigger() {
		
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