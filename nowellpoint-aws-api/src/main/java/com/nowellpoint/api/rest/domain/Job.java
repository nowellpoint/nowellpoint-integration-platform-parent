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

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.DateUtil;

public class Job extends AbstractResource {
	
	private Source source;
	
	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;
	
	private UserInfo owner;
	
	private String description;
	
	private String notificationEmail;
	
	private String slackWebhookUrl;
	
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
	
	private Set<JobExecution> jobExecutions = new HashSet<>();
	
	private Set<JobOutput> jobOutputs = new HashSet<>();

	private Job() {
		
	}
	
	private Job(
			String id,
			Source source,
			String jobName,
			String groupName,
			String className,
			Schedule schedule,
			String description,
			String notificationEmail,
			String slackWebhookUrl,
			String scheduleOption,
			UserInfo owner,
			Date createdOn,
			UserInfo createdBy,
			Date lastUpdatedOn,
			UserInfo lastUpdatedBy,
			Date nextFireTime) {
		
		if (Assert.isNotNull(description) && Assert.isEmpty(description)) {
			description = null;
		}
		
		if (Assert.isNotNull(notificationEmail) && Assert.isEmpty(notificationEmail)) {
			notificationEmail = null;
		}
		
		if (Assert.isNotNull(slackWebhookUrl) && Assert.isEmpty(slackWebhookUrl)) {
			slackWebhookUrl = null;
		}
		
		this.id = id;
		this.source = source;
		this.description = description;
		this.schedule = schedule;
		this.jobName = jobName;
		this.groupName = groupName;
		this.className = className;
		this.notificationEmail = notificationEmail;
		this.slackWebhookUrl = slackWebhookUrl;
		this.scheduleOption = scheduleOption;
		this.owner = owner;
		this.createdOn = createdOn;
		this.createdBy = createdBy;
		this.lastUpdatedOn = lastUpdatedOn;
		this.lastUpdatedBy = lastUpdatedBy;
		this.nextFireTime = nextFireTime;
		this.numberOfExecutions = 0;
		this.status = JobStatus.NEW;
	}
	
	public static Job of(CreateJobRequest jobRequest, UserInfo userInfo) {
		
		Schedule schedule = null;
		
		try {
			
			if (jobRequest.getSchedule().isPresent()) {
				schedule = Schedule.of(jobRequest.getSchedule().get());
			} else {
				String scheduleOption = jobRequest.getScheduleOption().get();
				if (JobScheduleOptions.RUN_WHEN_SUBMITTED.equals(scheduleOption)) {
					schedule = Schedule.of(RunWhenSubmitted.builder().build());
				} else if (JobScheduleOptions.RUN_ONCE.equals(scheduleOption)) {
					schedule = Schedule.of(RunOnce.builder().runDate(DateUtil.iso8601(jobRequest.getRunAt().get())).build());
				} else if (JobScheduleOptions.RUN_ON_SCHEDULE.equals(scheduleOption)) {
					schedule = Schedule.of(RunOnSchedule.builder()
							.endAt(DateUtil.iso8601(jobRequest.getEndAt().get()))
							.startAt(DateUtil.iso8601(jobRequest.getStartAt().get()))
							.timeInterval(Integer.valueOf(jobRequest.getTimeInterval().get()))
							.timeUnit(TimeUnit.valueOf(jobRequest.getTimeUnit().get()))
							.timeZone(TimeZone.getTimeZone(jobRequest.getTimeZone().get()))
							.build());
				} else if (JobScheduleOptions.RUN_ON_SPECIFIC_DAYS.equals(scheduleOption)) {		
					schedule = Schedule.of(RunOnSpecificDays.builder().build());
				} else {
					throw new IllegalArgumentException(String.format("Invalid Schedule Option: %s. Valid values are: RUN_WHEN_SUBMITTED, RUN_ONCE, RUN_ON_SCHEDULE or RUN_ON_SPECIFIC_DAYS", jobRequest.getScheduleOption().get()));
				}
			}
		} catch(ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		
		return new Job(
				null,
				jobRequest.getSource(),
				jobRequest.getJobType().getName(),
				jobRequest.getJobType().getGroup(),
				jobRequest.getJobType().getClassName(),
				schedule,
				jobRequest.getDescription().orElse(null),
				jobRequest.getNotificationEmail().orElse(null),
				jobRequest.getSlackWebhookUrl().orElse(null),
				jobRequest.getSchedule().isPresent() ? jobRequest.getSchedule().get().getScheduleOption() : jobRequest.getScheduleOption().get(),
				userInfo,
				Date.from(Instant.now()),
				userInfo,
				Date.from(Instant.now()),
				userInfo,
				schedule.getRunAt());
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

	public Set<JobExecution> getJobExecutions() {
		return jobExecutions;
	}

	public void setJobExecutions(Set<JobExecution> jobExecutions) {
		this.jobExecutions = jobExecutions;
	}
	
	public JobExecution getJobExecution(String fireInstanceId) {
		
		if (jobExecutions == null) {
			jobExecutions = new HashSet<>();
		}
		
		Optional<JobExecution> optional = jobExecutions.stream()
				.filter(s -> fireInstanceId.equals(s.getFireInstanceId()))
				.findFirst();
		
		if (! optional.isPresent()) {
			throw new IllegalArgumentException(String.format("Fire Instance Id: %s does not exist", fireInstanceId));
		}
		
		return optional.get();
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
	public synchronized com.nowellpoint.api.model.document.Job toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Job.class);
	}
}