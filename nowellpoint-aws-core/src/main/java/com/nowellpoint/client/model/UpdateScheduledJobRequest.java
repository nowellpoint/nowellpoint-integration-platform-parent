package com.nowellpoint.client.model;

import java.util.Date;

public class UpdateScheduledJobRequest {
	
	private String id;
	
	private String environmentKey;
	
	private String notificationEmail;
	
	private String description;
	
	private String connectorId;
	
	private Date scheduleDate;
	
	public UpdateScheduledJobRequest() {
		
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
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
	
	public UpdateScheduledJobRequest withId(String id) {
		setId(id);
		return this;
	}
	
	public UpdateScheduledJobRequest withEnvironmentKey(String environmentKey) {
		setEnvironmentKey(environmentKey);
		return this;
	}
	
	public UpdateScheduledJobRequest withNotificationEmail(String notificationEmail) {
		setNotificationEmail(notificationEmail);
		return this;
	}
	
	public UpdateScheduledJobRequest withDescription(String description) {
		setDescription(description);
		return this;
	}
	
	public UpdateScheduledJobRequest withConnectorId(String connectorId) {
		setConnectorId(connectorId);
		return this;
	}
	
	public UpdateScheduledJobRequest withScheduleDate(Date scheduleDate) {
		setScheduleDate(scheduleDate);
		return this;
	}
}