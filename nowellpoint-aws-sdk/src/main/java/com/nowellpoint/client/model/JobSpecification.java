package com.nowellpoint.client.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobSpecification extends AbstractResource {
	
	private UserInfo owner;
	
	private String jobId;
	
	private String deploymentStatus;
	
	private ConnectorInfo connector;
	
	private JobTypeInfo jobType;
	
	private String description;
	
	private Date start;
	
	private Date end;
	
	private String status;
	
	private Date lastRunDate;
	
	private String lastRunStatus;
	
	private String lastRunFailureMessage;
	
	private String notificationEmail;
	
	private String seconds;
	
	private String minutes;
	
	private String hours;
	
	private String dayOfMonth;
	
	private String month;
	
	private String dayOfWeek;
	
	private String year;
	
	private String timeZone;
	
	private List<RunHistory> runHistories;
	
	public JobSpecification() {
		setRunHistories(Collections.emptyList());
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

	public String getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(String deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
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

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastRunDate() {
		return lastRunDate;
	}

	public void setLastRunDate(Date lastRunDate) {
		this.lastRunDate = lastRunDate;
	}

	public String getLastRunStatus() {
		return lastRunStatus;
	}

	public void setLastRunStatus(String lastRunStatus) {
		this.lastRunStatus = lastRunStatus;
	}

	public String getNotificationEmail() {
		return notificationEmail;
	}

	public void setNotificationEmail(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}

	public String getLastRunFailureMessage() {
		return lastRunFailureMessage;
	}

	public void setLastRunFailureMessage(String lastRunFailureMessage) {
		this.lastRunFailureMessage = lastRunFailureMessage;
	}

	public List<RunHistory> getRunHistories() {
		return runHistories;
	}

	public void setRunHistories(List<RunHistory> runHistories) {
		this.runHistories = runHistories;
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
}