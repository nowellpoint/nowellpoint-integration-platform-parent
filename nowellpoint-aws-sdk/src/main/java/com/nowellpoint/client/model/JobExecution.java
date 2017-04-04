package com.nowellpoint.client.model;

import java.util.Date;
import java.util.List;

public class JobExecution {

	private String fireInstanceId;
	
	private Date fireTime;
	
	private Long jobRunTime;
	
	private String status;
	
	private String failureMessage;
	
	private List<JobOutput> jobOutputs;
	
	public JobExecution() {
		
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

	public List<JobOutput> getJobOutputs() {
		return jobOutputs;
	}

	public void setJobOutputs(List<JobOutput> jobOutputs) {
		this.jobOutputs = jobOutputs;
	}
}