package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PushTopic extends SObject {

	private static final long serialVersionUID = -2531020859064634486L;
	
	@JsonProperty("ApiVersion")
	private String apiVersion;
	
	@JsonProperty("Description")
	private String description;
	
	@JsonProperty("IsActive")
	private Boolean isActive;
	
	@JsonProperty("IsDeleted")
	private Boolean isDeleted;
	
	@JsonProperty("Name")
	private String name;
	
	@JsonProperty("NotifyForFields")
	private String notifyForFields;
	
	@JsonProperty("NotifyForOperationCreate")
	private Boolean notifyForOperationCreate;
	
	@JsonProperty("NotifyForOperationDelete")
	private Boolean notifyForOperationDelete;
	
	@JsonProperty("NotifyForOperations")
	private String notifyForOperations;
	
	@JsonProperty("NotifyForOperationUndelete")
	private Boolean notifyForOperationUndelete;
	
	@JsonProperty("NotifyForOperationUpdate")
	private Boolean notifyForOperationUpdate;
	
	@JsonProperty("Query")
	private String query;
	
	public PushTopic() {
		
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public String getName() {
		return name;
	}

	public String getNotifyForFields() {
		return notifyForFields;
	}

	public Boolean getNotifyForOperationCreate() {
		return notifyForOperationCreate;
	}

	public Boolean getNotifyForOperationDelete() {
		return notifyForOperationDelete;
	}

	public String getNotifyForOperations() {
		return notifyForOperations;
	}

	public Boolean getNotifyForOperationUndelete() {
		return notifyForOperationUndelete;
	}

	public Boolean getNotifyForOperationUpdate() {
		return notifyForOperationUpdate;
	}

	public String getQuery() {
		return query;
	}
}