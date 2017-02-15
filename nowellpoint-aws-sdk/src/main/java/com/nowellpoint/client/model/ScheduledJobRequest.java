package com.nowellpoint.client.model;

import java.util.Date;

public class ScheduledJobRequest {
	
	private String id;
	
	private String scheduledJobTypeId;
	
	private String environmentKey;
	
	private String notificationEmail;
	
	private String description;
	
	private String connectorId;
	
	private Date scheduleDate;
	
	public ScheduledJobRequest() {
		
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

	public String getScheduledJobTypeId() {
		return scheduledJobTypeId;
	}

	public void setScheduledJobTypeId(String scheduledJobTypeId) {
		this.scheduledJobTypeId = scheduledJobTypeId;
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
	
	public ScheduledJobRequest withId(String id) {
		setId(id);
		return this;
	}
	
	public ScheduledJobRequest withEnvironmentKey(String environmentKey) {
		setEnvironmentKey(environmentKey);
		return this;
	}
	
	public ScheduledJobRequest withNotificationEmail(String notificationEmail) {
		setNotificationEmail(notificationEmail);
		return this;
	}
	
	public ScheduledJobRequest withDescription(String description) {
		setDescription(description);
		return this;
	}
	
	public ScheduledJobRequest withScheduledJobTypeId(String scheduledJobTypeId) {
		setScheduledJobTypeId(scheduledJobTypeId);
		return this;
	}
	
	public ScheduledJobRequest withConnectorId(String connectorId) {
		setConnectorId(connectorId);
		return this;
	}
	
	public ScheduledJobRequest withScheduleDate(Date scheduleDate) {
		setScheduleDate(scheduleDate);
		return this;
	}
}