package com.nowellpoint.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobSpecification extends AbstractResource {
	
	private UserInfo owner;
	
	private String jobId;
	
	private ConnectorInfo connector;
	
	private JobTypeInfo jobType;
	
	private String description;
	
	private String notificationEmail;
	
	public JobSpecification() {

	}
	
	public JobSpecification(String id) {
		setId(id);
	}

	public UserInfo getOwner() {
		return owner;
	}

	public void setOwner(UserInfo owner) {
		this.owner = owner;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public ConnectorInfo getConnector() {
		return connector;
	}

	public void setConnector(ConnectorInfo connector) {
		this.connector = connector;
	}

	public JobTypeInfo getJobType() {
		return jobType;
	}

	public void setJobType(JobTypeInfo jobType) {
		this.jobType = jobType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNotificationEmail() {
		return notificationEmail;
	}

	public void setNotificationEmail(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}
}