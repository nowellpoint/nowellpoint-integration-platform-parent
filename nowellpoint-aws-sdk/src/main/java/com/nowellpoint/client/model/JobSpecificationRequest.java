package com.nowellpoint.client.model;

public class JobSpecificationRequest {
	
	private String id;
	
	private String jobTypeId;
	
	private String instanceKey;
	
	private String notificationEmail;
	
	private String description;
	
	private String connectorId;
	
	public JobSpecificationRequest() {
		
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getInstanceKey() {
		return instanceKey;
	}

	public void setInstanceKey(String instanceKey) {
		this.instanceKey = instanceKey;
	}

	public String getNotificationEmail() {
		return notificationEmail;
	}

	public void setNotificationEmail(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getJobTypeId() {
		return jobTypeId;
	}

	public void setJobTypeId(String jobTypeId) {
		this.jobTypeId = jobTypeId;
	}

	public String getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(String connectorId) {
		this.connectorId = connectorId;
	}

	public JobSpecificationRequest withId(String id) {
		setId(id);
		return this;
	}
	
	public JobSpecificationRequest withInstanceKey(String instanceKey) {
		setInstanceKey(instanceKey);
		return this;
	}
	
	public JobSpecificationRequest withNotificationEmail(String notificationEmail) {
		setNotificationEmail(notificationEmail);
		return this;
	}
	
	public JobSpecificationRequest withDescription(String description) {
		setDescription(description);
		return this;
	}
	
	public JobSpecificationRequest withJobTypeId(String jobTypeId) {
		setJobTypeId(jobTypeId);
		return this;
	}
	
	public JobSpecificationRequest withConnectorId(String connectorId) {
		setConnectorId(connectorId);
		return this;
	}
}