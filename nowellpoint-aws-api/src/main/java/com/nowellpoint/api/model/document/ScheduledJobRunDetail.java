package com.nowellpoint.api.model.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.api.model.codec.ScheduledJobRunDetailCodec;
import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.document.MongoDocument;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="scheduled.job.run.details", codec=ScheduledJobRunDetailCodec.class)
public class ScheduledJobRunDetail extends MongoDocument {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -506938745277443964L;
	
	private String fireInstanceId;
	
	private String groupName;
	
	private String jobName;
	
	private Long jobRunTime;
	
	private String status;
	
	private String errorMessage;

	public ScheduledJobRunDetail() {
		
	}

	public String getFireInstanceId() {
		return fireInstanceId;
	}

	public void setFireInstanceId(String fireInstanceId) {
		this.fireInstanceId = fireInstanceId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}