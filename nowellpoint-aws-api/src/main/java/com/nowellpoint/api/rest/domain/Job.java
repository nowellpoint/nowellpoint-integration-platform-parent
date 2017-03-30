/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.nowellpoint.api.rest.domain;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class Job extends AbstractResource {
	
	private Source source;
	
	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;
	
	private UserInfo owner;
	
	private String description;
	
	private String notificationEmail;
	
	private String scheduleOption;
	
	private String jobName;
	
	@JsonIgnore
	private String className;
	
	private Schedule schedule;
	
	private String groupName;
	
	private Long jobRunTime;
	
	private Date fireTime;
	
	private Date nextFireTime;
	
	private String status;
	
	private String failureMessage;
	
	private Integer numberOfExecutions;
	
	private Set<JobHistory> jobHistory = new HashSet<JobHistory>();

	private Job() {
		
	}
	
	private Job(
			Source source,
			JobType jobType,
			Schedule schedule,
			String description,
			String notificationEmail,
			String scheduleOption,
			UserInfo owner,
			Date createdOn,
			UserInfo createdBy,
			Date lastUpdatedOn,
			UserInfo lastUpdatedBy,
			Integer numberOfExecutions) {
		
		this.source = source;
		this.description = description;
		this.schedule = schedule;
		this.jobName = jobType.getName();
		this.groupName = jobType.getGroup();
		this.className = jobType.getClassName();
		this.notificationEmail = notificationEmail;
		this.scheduleOption = scheduleOption;
		this.owner = owner;
		this.createdOn = createdOn;
		this.createdBy = createdBy;
		this.lastUpdatedOn = lastUpdatedOn;
		this.lastUpdatedBy = lastUpdatedBy;
		this.numberOfExecutions = 0;
	}
	
	public static Job of(
			Source source,
			JobType jobType,
			Schedule schedule,
			UserInfo userInfo,
			String description,
			String notificationEmail,
			String scheduleOption) {
		
		if (Assert.isEmpty(description)) {
			description = null;
		}
		
		return new Job(
				source,
				jobType,
				schedule,
				description,
				notificationEmail,
				scheduleOption,
				userInfo,
				Date.from(Instant.now()),
				userInfo,
				Date.from(Instant.now()),
				userInfo,
				0);
	}
	
	private <T> Job(T document) {
		modelMapper.map(document, this);
	}
	
	public static Job of(MongoDocument document) {
		return new Job(document);
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
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

	public String getScheduleOption() {
		return scheduleOption;
	}

	public void setScheduleOption(String scheduleOption) {
		this.scheduleOption = scheduleOption;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Long getJobRunTime() {
		return jobRunTime;
	}

	public void setJobRunTime(Long jobRunTime) {
		this.jobRunTime = jobRunTime;
	}

	public Date getFireTime() {
		return fireTime;
	}

	public void setFireTime(Date fireTime) {
		this.fireTime = fireTime;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
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

	public Integer getNumberOfExecutions() {
		return numberOfExecutions;
	}

	public void setNumberOfExecutions(Integer numberOfExecutions) {
		this.numberOfExecutions = numberOfExecutions;
	}

	public Set<JobHistory> getJobHistory() {
		return jobHistory;
	}

	public void setJobHistory(Set<JobHistory> jobHistory) {
		this.jobHistory = jobHistory;
	}
	
	public void addJobHistory(JobHistory jobHistory) {
		this.jobHistory.add(jobHistory);
	}

	@Override
	public com.nowellpoint.api.model.document.Job toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Job.class);
	}
	
	public class Statuses {
		public static final String SCHEDULED = "Scheduled"; 
		public static final String STOPPED = "Stopped";
		public static final String TERMINATED = "Terminated";
	}
	
	public class ScheduleOptions {
		public static final String RUN_WHEN_SUBMITTED = "RUN_WHEN_SUBMITTED";
	}
}