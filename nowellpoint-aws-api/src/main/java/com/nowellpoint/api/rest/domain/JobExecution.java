package com.nowellpoint.api.rest.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class JobExecution {

	private String fireInstanceId;
	
	private Date fireTime;
	
	private Long jobRunTime;
	
	private String status;
	
	private String failureMessage;
	
	private Set<JobOutput> jobOutputs;
	
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

	public Set<JobOutput> getJobOutputs() {
		return jobOutputs;
	}

	public void setJobOutputs(Set<JobOutput> jobOutputs) {
		this.jobOutputs = jobOutputs;
	}
	
	public JobOutput getJobOutput(String filename) {
		
		if (jobOutputs == null) {
			jobOutputs = new HashSet<>();
		}
		
		Optional<JobOutput> optional = jobOutputs.stream()
				.filter(s -> filename.equals(s.getFilename()))
				.findFirst();
		
		if (! optional.isPresent()) {
			throw new IllegalArgumentException(String.format("Filename: %s does not exist", filename));
		}
		
		return optional.get();
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