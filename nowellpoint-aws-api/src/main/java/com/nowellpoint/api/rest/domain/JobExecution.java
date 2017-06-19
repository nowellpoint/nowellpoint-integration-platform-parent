package com.nowellpoint.api.rest.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;

public class JobExecution {

	private String fireInstanceId;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date fireTime;
	
	private Long jobRunTime;
	
	private String status;
	
	private String failureMessage;
	
	private JobExecution() {
		
	}
	
	private JobExecution(String fireInstanceId, Date fireTime, Long jobRunTime, String status, String failureMessage) {
		this.fireInstanceId = fireInstanceId;
		this.fireTime = fireTime;
		this.jobRunTime = jobRunTime;
		this.status = status;
		this.failureMessage = failureMessage;
	}
	
	public static JobExecution of(String fireInstanceId, Date fireTime, Long jobRunTime, String status, String failureMessage) {
		return new JobExecution(
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
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(this.fireInstanceId)
		        .toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { 
			return false;
		}
		if (obj == this) { 
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		JobExecution jobExecution = (JobExecution) obj;
		return new EqualsBuilder()
				.append(this.fireInstanceId, jobExecution.fireInstanceId)
				.isEquals();
	}
}