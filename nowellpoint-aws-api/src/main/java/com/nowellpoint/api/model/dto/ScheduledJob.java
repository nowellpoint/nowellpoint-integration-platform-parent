package com.nowellpoint.api.model.dto;

import java.util.Date;
import java.util.Set;

import org.hibernate.validator.constraints.Email;

import com.nowellpoint.api.model.document.RunHistory;

public class ScheduledJob extends AbstractResource {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1699588438360478864L;
	
	private AccountProfile createdBy;
	
	private AccountProfile lastModifiedBy;
	
	private AccountProfile owner;
	
	private String environmentKey;
	
	private String environmentName;
	
	private Boolean isSandbox;
	
	private String connectorId;
	
	private String connectorType;
	
	private String jobTypeId;
	
	private String jobTypeCode;
	
	private String jobTypeName;
	
	private String description;
	
	private Date scheduleDate;
	
	private String status;
	
	private Date lastRunDate;
	
	private String lastRunStatus;
	
	private String lastRunFailureMessage;
	
	@Email
	private String notificationEmail;
	
	private Set<RunHistory> runHistories;
	
	public ScheduledJob() {
		
	}

	public AccountProfile getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(AccountProfile createdBy) {
		this.createdBy = createdBy;
	}

	public AccountProfile getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(AccountProfile lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public AccountProfile getOwner() {
		return owner;
	}

	public void setOwner(AccountProfile owner) {
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
}