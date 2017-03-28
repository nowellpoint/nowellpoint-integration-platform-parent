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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class Job extends AbstractResource {
	
	public static final String SCHEDULED = "Scheduled"; 
	public static final String STOPPED = "Stopped";
	public static final String TERMINATED = "Terminated";
	
	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;
	
	private UserInfo owner;
	
	private String description;
	
	private String notificationEmail;
	
	private String scheduleOption;
	
	private String jobName;
	
	@JsonIgnore
	private String className;
	
	private String groupName;
	
	private Long jobRunTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date start;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date end;
	
	private String timeZone;
	
	private String seconds;
	
	private String minutes;
	
	private String hours;
	
	private String dayOfMonth;
	
	private String month;
	
	private String dayOfWeek;
	
	private String year;
	
	private String status;
	
	private String failureMessage;
	
	private Integer numberOfExecutions;

	public Job() {
		
	}
	
	private Job(
			JobType jobType,
			String dayOfMonth,
			String dayOfWeek,
			String description,
			String hours,
			Date end,
			String minutes,
			String month,
			String notificationEmail,
			String scheduleOption,
			String seconds,
			Date start,
			String timeZone,
			String year,
			UserInfo owner,
			Date createdOn,
			UserInfo createdBy,
			Date lastUpdatedOn,
			UserInfo lastUpdatedBy,
			Integer numberOfExecutions) {
		
		this.dayOfMonth = dayOfMonth;
		this.dayOfWeek = dayOfWeek;
		this.description = description;
		this.hours = hours;
		this.jobName = jobType.getName();
		this.groupName = jobType.getGroup();
		this.className = jobType.getClassName();
		this.end = end;
		this.minutes = minutes;
		this.month = month;
		this.notificationEmail = notificationEmail;
		this.scheduleOption = scheduleOption;
		this.seconds = seconds;
		this.start = start;
		this.timeZone = timeZone;
		this.year = year;
		this.owner = owner;
		this.createdOn = createdOn;
		this.createdBy = createdBy;
		this.lastUpdatedOn = lastUpdatedOn;
		this.lastUpdatedBy = lastUpdatedBy;
		this.numberOfExecutions = numberOfExecutions;
	}
	
	public static Job of(
			JobType jobType,
			UserInfo userInfo,
			String dayOfMonth,
			String dayOfWeek,
			String description,
			String hours,
			Date end,
			String minutes,
			String month,
			String notificationEmail,
			String scheduleOption,
			String seconds,
			Date start,
			String timeZone,
			String year) {
		
		if (Assert.isEmpty(dayOfMonth)) {
			dayOfMonth = null;
		}
		
		if (Assert.isEmpty(dayOfWeek)) {
			dayOfWeek = null;
		}
		
		if (Assert.isEmpty(description)) {
			description = null;
		}
		
		if (Assert.isEmpty(hours)) {
			hours = null;
		}
		
		if (Assert.isEmpty(minutes)) {
			minutes = null;
		}
		
		if (Assert.isEmpty(month)) {
			month = null;
		}
		
		if (Assert.isEmpty(seconds)) {
			seconds = null;
		}
		
		if (Assert.isEmpty(year)) {
			year = null;
		}
		
		return new Job(
				jobType,
				dayOfMonth,
				dayOfWeek,
				description,
				hours,
				end,
				minutes,
				month,
				notificationEmail,
				scheduleOption,
				seconds,
				start,
				timeZone,
				year,
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

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getSeconds() {
		return seconds;
	}

	public void setSeconds(String seconds) {
		this.seconds = seconds;
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
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

	@Override
	public com.nowellpoint.api.model.document.Job toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.Job.class);
	}
}