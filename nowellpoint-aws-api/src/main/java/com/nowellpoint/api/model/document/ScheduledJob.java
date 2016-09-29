package com.nowellpoint.api.model.document;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.model.codec.ScheduledJobCodec;
import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.document.DateDeserializer;
import com.nowellpoint.mongodb.document.DateSerializer;
import com.nowellpoint.mongodb.document.MongoDocument;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="scheduled.jobs", codec=ScheduledJobCodec.class)
public class ScheduledJob extends MongoDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 4880299116047933778L;
	
	private User owner;
	
	private String environmentKey;
	
	private String environmentName;
	
	private Boolean isSandbox;
	
	private String connectorId;
	
	private String connectorType;
	
	private String jobTypeId;
	
	private String jobTypeCode;
	
	private String jobTypeName;
	
	private String description;
	
	@JsonSerialize(using = DateSerializer.class)
	@JsonDeserialize(using = DateDeserializer.class)
	private Date scheduleDate;
	
	private String status;
	
	@JsonSerialize(using = DateSerializer.class)
	@JsonDeserialize(using = DateDeserializer.class)
	private Date lastRunDate;
	
	private String lastRunStatus;
	
	private String lastRunFailureMessage;
	
	private String notificationEmail;
	
	private Set<RunHistory> runHistories;
	
	private Integer year;
	
	private Integer month;
	
	private Integer day;
	
	private Integer hour;
	
	private Integer minute;
	
	private Integer second;
	
	public ScheduledJob() {
		
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getEnvironmentKey() {
		return environmentKey;
	}

	public void setEnvironmentKey(String environmentKey) {
		this.environmentKey = environmentKey;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public Boolean getIsSandbox() {
		return isSandbox;
	}

	public void setIsSandbox(Boolean isSandbox) {
		this.isSandbox = isSandbox;
	}

	public String getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(String connectorId) {
		this.connectorId = connectorId;
	}

	public String getConnectorType() {
		return connectorType;
	}

	public void setConnectorType(String connectorType) {
		this.connectorType = connectorType;
	}

	public String getJobTypeId() {
		return jobTypeId;
	}

	public void setJobTypeId(String jobTypeId) {
		this.jobTypeId = jobTypeId;
	}

	public String getJobTypeCode() {
		return jobTypeCode;
	}

	public void setJobTypeCode(String jobTypeCode) {
		this.jobTypeCode = jobTypeCode;
	}

	public String getJobTypeName() {
		return jobTypeName;
	}

	public void setJobTypeName(String jobTypeName) {
		this.jobTypeName = jobTypeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
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

	public String getLastRunFailureMessage() {
		return lastRunFailureMessage;
	}

	public void setLastRunFailureMessage(String lastRunFailureMessage) {
		this.lastRunFailureMessage = lastRunFailureMessage;
	}

	public String getNotificationEmail() {
		return notificationEmail;
	}

	public void setNotificationEmail(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}

	public Set<RunHistory> getRunHistories() {
		return runHistories;
	}

	public void setRunHistories(Set<RunHistory> runHistories) {
		this.runHistories = runHistories;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getHour() {
		return hour;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public Integer getMinute() {
		return minute;
	}

	public void setMinute(Integer minute) {
		this.minute = minute;
	}

	public Integer getSecond() {
		return second;
	}

	public void setSecond(Integer second) {
		this.second = second;
	}
	
	public void addRunHistory(RunHistory runHistory) {
		if (runHistories == null) {
			runHistories = new HashSet<RunHistory>();
		}
		if (runHistories.size() == 10) {
			runHistories.stream().sorted((r1, r2) -> r1.getFireTime().compareTo(r2.getFireTime())).collect(Collectors.toList()).remove(0);
		}
		runHistories.add(runHistory);
	}
}