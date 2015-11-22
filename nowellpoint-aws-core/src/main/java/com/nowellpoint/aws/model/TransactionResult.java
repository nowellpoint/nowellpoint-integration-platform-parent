package com.nowellpoint.aws.model;

import java.util.Set;

public class TransactionResult {

	public String userId;
	
	public String organizationId;
	
	public String executionTime;
	
	public Set<String> eventResponses;
	
	public TransactionResult() {
		
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}

	public Set<String> getEventResponses() {
		return eventResponses;
	}

	public void setEventResponses(Set<String> eventResponses) {
		this.eventResponses = eventResponses;
	}
}