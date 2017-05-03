package com.nowellpoint.client.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JobRequest {
	
	private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String notificationEmail;
	
	private String slackWebhookUrl;
	
	private String scheduleOption;
	
	private String description;
	
	private String runAt;
	
	private String startAt;
	
	private String endAt;
	
	private String timeZone;
	
	private String timeUnit;
	
	private Integer timeInterval;
	
	private String jobTypeId;
	
	private String connectorId;
	
	private String seconds;
	
	private String minutes;
	
	private String hours;
	
	private String dayOfMonth;
	
	private String month;
	
	private String dayOfWeek;
	
	private String year; 
	
	public JobRequest() {
		
	}

	public String getNotificationEmail() {
		return notificationEmail;
	}

	public void setNotificationEmail(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}

	public String getSlackWebhookUrl() {
		return slackWebhookUrl;
	}

	public void setSlackWebhookUrl(String slackWebhookUrl) {
		this.slackWebhookUrl = slackWebhookUrl;
	}

	public String getScheduleOption() {
		return scheduleOption;
	}

	public void setScheduleOption(String scheduleOption) {
		this.scheduleOption = scheduleOption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRunAt() {
		return runAt;
	}

	public void setRunAt(String runAt) {
		this.runAt = runAt;
	}

	public String getStartAt() {
		return startAt;
	}

	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}

	public String getEndAt() {
		return endAt;
	}

	public void setEndAt(String endAt) {
		this.endAt = endAt;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	public Integer getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(Integer timeInterval) {
		this.timeInterval = timeInterval;
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
	
	public JobRequest withNotificationEmail(String notificationEmail) {
		setNotificationEmail(notificationEmail);
		return this;
	}
	
	public JobRequest withSlackWebhookUrl(String slackWebhookUrl) {
		setSlackWebhookUrl(slackWebhookUrl);
		return this;
	}
	
	public JobRequest withDescription(String description) {
		setDescription(description);
		return this;
	}
	
	public JobRequest withRunAt(Date runAt) {
		setRunAt(dateTimeFormat.format(runAt));
		return this;
	}
	
	public JobRequest withRunAt(String runAt) {
		setRunAt(runAt);
		return this;
	}
	
	public JobRequest withScheduleOption(String scheduleOption) {
		setScheduleOption(scheduleOption);
		return this;
	}
	
	public JobRequest withStartAt(Date startAt) {
		setStartAt(dateTimeFormat.format(startAt));
		return this;
	}
	
	public JobRequest withStartAt(String startAt) {
		setStartAt(startAt);
		return this;
	}
	
	public JobRequest withEndAt(Date endAt) {
		setEndAt(dateTimeFormat.format(endAt));
		return this;
	}
	
	public JobRequest withEndAt(String endAt) {
		setEndAt(endAt);
		return this;
	}
	
	public JobRequest withTimeZone(String timeZone) {
		setTimeZone(timeZone);
		return this;
	}
	
	public JobRequest withTimeUnit(String timeUnit) {
		setTimeUnit(timeUnit);
		return this;
	}
	
	public JobRequest withTimeInterval(Integer timeInterval) {
		setTimeInterval(timeInterval);
		return this;
	}
	
	public JobRequest withTimeInterval(String timeInterval) {
		setTimeInterval(Integer.valueOf(timeInterval));
		return this;
	}
	
	public JobRequest withJobTypeId(String jobTypeId) {
		setJobTypeId(jobTypeId);
		return this;
	}
	
	public JobRequest withConnectorId(String connectorId) {
		setConnectorId(connectorId);
		return this;
	}
	
	public JobRequest withSeconds(String seconds) {
		setSeconds(seconds);
		return this;
	}
	
	public JobRequest withMinutes(String minutes) {
		setMinutes(minutes);
		return this;
	}
	
	public JobRequest withHours(String hours) {
		setHours(hours);
		return this;
	}
	
	public JobRequest withDayOfMonth(String dayOfMonth) {
		setDayOfMonth(dayOfMonth);
		return this;
	}
	
	public JobRequest withMonth(String month) {
		setMonth(month);
		return this;
	}
	
	public JobRequest withDayOfWeek(String dayOfWeek) {
		setDayOfWeek(dayOfWeek);
		return this;
	}
	
	public JobRequest withYear(String year) {
		setYear(year);
		return this;
	}
}