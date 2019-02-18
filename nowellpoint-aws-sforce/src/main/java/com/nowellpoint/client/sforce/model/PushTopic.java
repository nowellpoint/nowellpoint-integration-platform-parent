package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class PushTopic extends SObject {

	private static final long serialVersionUID = -2531020859064634486L;
	
	public static final String QUERY = "Select "
			+ "ApiVersion, "
			+ "Description, "
			+ "IsActive, "
			+ "IsDeleted, "
			+ "CreatedDate, "
			+ "Id, "
			+ "LastModifiedById, "
			+ "LastModifiedDate, "
			+ "Name, "
			+ "NotifyForFields, "
			+ "NotifyForOperationCreate, "
			+ "NotifyForOperationDelete, "
			+ "NotifyForOperations, "
			+ "NotifyForOperationUndelete, "
			+ "NotifyForOperationUpdate, "
			+ "Query "
			+ "From PushTopic";
	
	@Getter @JsonProperty("ApiVersion") private String apiVersion;
	@Getter @JsonProperty("Description") private String description;
	@Getter @JsonProperty("IsActive") private Boolean isActive;
	@Getter @JsonProperty("IsDeleted") private Boolean isDeleted;
	@Getter @JsonProperty("Name") private String name;
	@Getter @JsonProperty("NotifyForFields") private String notifyForFields;
	@Getter @JsonProperty("NotifyForOperationCreate") private Boolean notifyForOperationCreate;
	@Getter @JsonProperty("NotifyForOperationDelete") private Boolean notifyForOperationDelete;
	@Getter @JsonProperty("NotifyForOperations") private String notifyForOperations;
	@Getter @JsonProperty("NotifyForOperationUndelete") private Boolean notifyForOperationUndelete;
	@Getter @JsonProperty("NotifyForOperationUpdate") private Boolean notifyForOperationUpdate;
	@Getter @JsonProperty("Query") private String query;
	
	public PushTopic() {
		
	}
}