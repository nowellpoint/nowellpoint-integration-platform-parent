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

package com.nowellpoint.api.model.document;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.annotation.EmbedMany;
import com.nowellpoint.mongodb.annotation.EmbedOne;
import com.nowellpoint.mongodb.annotation.Reference;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

@Document(collectionName="jobs")
public class Job extends MongoDocument {
	
	private static final long serialVersionUID = -8426321555023081859L;
	
	@EmbedOne
	private Meta meta;
	
	@EmbedOne
	private Source source;
	
	@Reference(referenceClass = AccountProfile.class)
	private UserRef createdBy;
	
	@Reference(referenceClass = AccountProfile.class)
	private UserRef lastUpdatedBy;
	
	@Reference(referenceClass = AccountProfile.class)
	private UserRef owner;
	
	private String description;
	
	private String notificationEmail;
	
	private String slackWebhookUrl;
	
	private String scheduleOption;
	
	@EmbedOne
	private Schedule schedule;
	
	private String groupName;
	
	private String jobName;
	
	private String className;
	
	private Long jobRunTime;
	
	private Date fireTime;
	
	private Date nextFireTime;
	
	private String status;
	
	private String failureMessage;
	
	private Integer numberOfExecutions;
	
	@EmbedMany
	private Set<JobExecution> jobExecutions = new HashSet<>();
	
	@EmbedMany
	private Set<JobOutput> jobOutputs = new HashSet<>();

	public Job() {
		
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public UserRef getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserRef createdBy) {
		this.createdBy = createdBy;
	}

	public UserRef getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserRef lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public UserRef getOwner() {
		return owner;
	}
	
	public void setOwner(UserRef owner) {
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

	public String getSlackWebhookUrl() {
		return slackWebhookUrl;
	}

	public void setSlackWebhookUrl(String slackWebhookUrl) {
		this.slackWebhookUrl = slackWebhookUrl;
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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
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
	
	public Set<JobExecution> getJobExecutions() {
		return jobExecutions;
	}

	public void setJobExecutions(Set<JobExecution> jobExecutions) {
		this.jobExecutions = jobExecutions;
		if (this.jobExecutions.size() > 45) {
			this.jobExecutions = limit(this.jobExecutions);
		}
	}

	public Set<JobOutput> getJobOutputs() {
		return jobOutputs;
	}

	public void setJobOutputs(Set<JobOutput> jobOutputs) {
		this.jobOutputs = jobOutputs;
	}

	public void addJobExecution(JobExecution jobExecution) {
		if (Assert.isNull(this.jobExecutions)) {
			this.jobExecutions = new HashSet<>();
		}
		this.jobExecutions.add(jobExecution);
		if (this.jobExecutions.size() > 45) {
			this.jobExecutions = limit(this.jobExecutions);
		}
	}
	
	public void addJobOutput(JobOutput jobOutput) {
		if (Assert.isNull(this.jobOutputs)) {
			this.jobOutputs = new HashSet<>();
		}
		this.jobOutputs.add(jobOutput);
	}
	
	private Set<JobExecution> limit(Set<JobExecution> jobExecutions) {
		return jobExecutions.stream()
				.sorted((p1,p2) -> p2.getFireTime().compareTo(p1.getFireTime()))
				.limit(45)
				.collect(Collectors.toSet());
	}
}