package com.nowellpoint.api.rest.domain;

import java.util.Date;
import java.util.Set;

public class JobHistory {

	private String fireInstanceId;
	
	private Date fireTime;
	
	private Long jobRunTime;
	
	private String status;
	
	private String failureMessage;
	
	private Set<Backup> backups;
	
	private JobHistory() {
		
	}
	
	private JobHistory(String fireInstanceId, Date fireTime, Long jobRunTime, String status, String failureMessage) {
		this.fireInstanceId = fireInstanceId;
		this.fireTime = fireTime;
		this.jobRunTime = jobRunTime;
		this.status = status;
		this.failureMessage = failureMessage;
	}
	
	public static JobHistory of(String fireInstanceId, Date fireTime, Long jobRunTime, String status, String failureMessage) {
		return new JobHistory(
				fireInstanceId, 
				fireTime, 
				jobRunTime, 
				status, 
				failureMessage);
	}

	public String getFireInstanceId() {
		return fireInstanceId;
	}

	public void setFireInstanceId(String fireInstanceId) {
		this.fireInstanceId = fireInstanceId;
	}

	public Date getFireTime() {
		return fireTime;
	}

	public void setFireTime(Date fireTime) {
		this.fireTime = fireTime;
	}

	public Long getJobRunTime() {
		return jobRunTime;
	}

	public void setJobRunTime(Long jobRunTime) {
		this.jobRunTime = jobRunTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}

	public Set<Backup> getBackups() {
		return backups;
	}

	public void setBackups(Set<Backup> backups) {
		this.backups = backups;
	}
}