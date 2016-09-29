package com.nowellpoint.client.model;

import java.util.Date;

public class CreateScheduledJobRequest {
	
	private String environmentKey;
	
	private String notificationEmail;
	
	private String description;
	
	private String jobTypeId;
	
	private String connectorId;
	
	private Date scheduleDate;
	
	public CreateScheduledJobRequest() {
		
	}

	public String getEnvironmentKey() {
		return environmentKey;
	}

	public void setEnvironmentKey(String environmentKey) {
		this.environmentKey = environmentKey;
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

	public Date getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
	
	public CreateScheduledJobRequest withEnvironmentKey(String environmentKey) {
		setEnvironmentKey(environmentKey);
		return this;
	}
	
	public CreateScheduledJobRequest withNotificationEmail(String notificationEmail) {
		setNotificationEmail(notificationEmail);
		return this;
	}
	
	public CreateScheduledJobRequest withDescription(String description) {
		setDescription(description);
		return this;
	}
	
	public CreateScheduledJobRequest withJobTypeId(String jobTypeId) {
		setJobTypeId(jobTypeId);
		return this;
	}
	
	public CreateScheduledJobRequest withConnectorId(String connectorId) {
		setConnectorId(connectorId);
		return this;
	}
	
	public CreateScheduledJobRequest withScheduleDate(Date scheduleDate) {
		setScheduleDate(scheduleDate);
		return this;
	}
}