package com.nowellpoint.client.model;

import java.util.Date;

public class JobScheduleRequest {
	
	private String id;
	
	private String scheduledJobTypeId;
	
	private String environmentKey;
	
	private String notificationEmail;
	
	private String description;
	
	private String connectorId;
	
	private Date scheduleDate;
	
	private String seconds;
	
	private String minutes;
	
	private String hours;
	
	private String dayOfMonth;
	
	private String month;
	
	private String dayOfWeek;
	
	private String year;
	
	public JobScheduleRequest() {
		
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
	
	public String getSeconds() {
		return seconds;
	}

	public void setSeconds(String seconds) {
		this.seconds = seconds;
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public JobScheduleRequest withId(String id) {
		setId(id);
		return this;
	}
	
	public JobScheduleRequest withEnvironmentKey(String environmentKey) {
		setEnvironmentKey(environmentKey);
		return this;
	}
	
	public JobScheduleRequest withNotificationEmail(String notificationEmail) {
		setNotificationEmail(notificationEmail);
		return this;
	}
	
	public JobScheduleRequest withDescription(String description) {
		setDescription(description);
		return this;
	}
	
	public JobScheduleRequest withScheduledJobTypeId(String scheduledJobTypeId) {
		setScheduledJobTypeId(scheduledJobTypeId);
		return this;
	}
	
	public JobScheduleRequest withConnectorId(String connectorId) {
		setConnectorId(connectorId);
		return this;
	}
	
	public JobScheduleRequest withScheduleDate(Date scheduleDate) {
		setScheduleDate(scheduleDate);
		return this;
	}
	
	public JobScheduleRequest withSeconds(String seconds) {
		setSeconds(seconds);
		return this;
	}
	
	public JobScheduleRequest withMinutes(String minutes) {
		setMinutes(minutes);
		return this;
	}
	
	public JobScheduleRequest withHours(String hours) {
		setHours(hours);
		return this;
	}
	
	public JobScheduleRequest withDayOfMonth(String dayOfMonth) {
		setDayOfMonth(dayOfMonth);
		return this;
	}
	
	public JobScheduleRequest withMonth(String month) {
		setMonth(month);
		return this;
	}
	
	public JobScheduleRequest withDayOfWeek(String dayOfWeek) {
		setDayOfWeek(dayOfWeek);
		return this;
	}
	
	public JobScheduleRequest withYear(String year) {
		setYear(year);
		return this;
	}
}