package com.nowellpoint.api.rest.domain;

import java.time.Instant;
import java.util.Date;

import org.hibernate.validator.constraints.Email;

import com.nowellpoint.mongodb.document.MongoDocument;

public class JobSpecification extends AbstractResource {

	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;
	
	private UserInfo owner;
	
	private String jobId;
	
	private ConnectorInfo connector;
	
	private JobTypeInfo jobType;
	
	private String description;
	
	private String notificationEmail;
	
	public JobSpecification() {
		
	}
	
	private <T> JobSpecification(T document) {
		modelMapper.map(document, this);
	}
	
	public static JobSpecification of(MongoDocument document) {
		return new JobSpecification(document);
	}

	public UserInfo getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserInfo createdBy) {
		this.createdBy = createdBy;
	}

	public UserInfo getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserInfo lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
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
	
	public String getNotificationEmail() {
		return notificationEmail;
	}

	public void setNotificationEmail(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}

	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.JobSpecification.class);
	}
	
	public static JobSpecificationBuilder builder() {
		return new JobSpecificationBuilder();
	}
	
	public static JobSpecificationBuilder builder(JobSpecification original) {
		return new JobSpecificationBuilder(original);
	}
	
	public static class JobSpecificationBuilder {
		
		private String createdBy;
		
		private String lastUpdatedBy;
		
		private String owner;
		
		private String jobId;
		
		private String description;
		
		private String jobType;
		
		@Email
		private String notificationEmail;
		
		private JobSpecificationBuilder() {
			
		}
		
		private JobSpecificationBuilder(JobSpecification jobSpecification) {
			createdBy = jobSpecification.getCreatedBy().getId();
			lastUpdatedBy = jobSpecification.getLastUpdatedBy().getId();
			owner = jobSpecification.getOwner().getId();
			jobId = jobSpecification.getJobId();
			description = jobSpecification.getDescription();
			//jobType = jobSpecification.getJobType();
			notificationEmail = jobSpecification.getNotificationEmail();
		}
		
		public JobSpecificationBuilder withCreatedBy(String createdBy) {
			this.createdBy = createdBy;
			return this;
		}
		
		public JobSpecificationBuilder withLastUpdatedBy(String lastUpdatedBy) {
			this.lastUpdatedBy = lastUpdatedBy;
			return this;
		}
		
		public JobSpecificationBuilder withOwner(String owner) {
			this.owner = owner;
			return this;
		}
		
		public JobSpecificationBuilder withJobId(String jobId) {
			this.jobId = jobId;
			return this;
		}
		
		public JobSpecificationBuilder withDescription(String description) {
			this.description = description;
			return this;
		}
		
		public JobSpecificationBuilder withJobType(String jobType) {
			this.jobType = jobType;
			return this;
		}
		
		public JobSpecificationBuilder withNotificationEmail(String notificationEmail) {
			this.notificationEmail = notificationEmail;
			return this;
		}
		
		public JobSpecification build() {
			JobSpecification jobSpecification = new JobSpecification();
			jobSpecification.setCreatedBy(new UserInfo(createdBy));
			jobSpecification.setLastUpdatedBy(new UserInfo(lastUpdatedBy));
			jobSpecification.setCreatedOn(Date.from(Instant.now()));
			jobSpecification.setLastUpdatedOn(Date.from(Instant.now()));
			jobSpecification.setOwner(new UserInfo(owner));
			jobSpecification.setJobId(jobId);
			jobSpecification.setDescription(description);
			//jobSpecification.setJobType(jobType);
			jobSpecification.setNotificationEmail(notificationEmail);
			return jobSpecification;
		}
	}
}