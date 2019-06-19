package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
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
	
	@Getter @JsonProperty("Body") private String body;
	@Getter @JsonProperty("IsValid") private Boolean isValid; 
	@Getter @JsonProperty("ApiVersion") private Double apiVersion;
	@Getter @JsonProperty("Name") private String name;
}