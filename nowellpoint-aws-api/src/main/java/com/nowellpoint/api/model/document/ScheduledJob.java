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
import java.util.Set;

import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.annotation.EmbedMany;
import com.nowellpoint.mongodb.annotation.EmbedOne;
import com.nowellpoint.mongodb.annotation.Reference;
import com.nowellpoint.mongodb.document.MongoDocument;

@Document(collectionName="scheduled.jobs")
public class ScheduledJob extends MongoDocument {

	private static final long serialVersionUID = 4880299116047933778L;
	
	@EmbedOne
	private Meta meta;
	
	@Reference
	private UserInfo createdBy;
	
	@Reference
	private UserInfo lastUpdatedBy;
	
	@Reference
	private UserInfo owner;
	
	private String environmentKey;
	
	private String environmentName;
	
	private Boolean isSandbox;
	
	private String connectorId;
	
	@EmbedOne
	private JobType jobType;
	
	private String description;
	
	private Date scheduleDate;
	
	private String status;
	
	private Date lastRunDate;
	
	private String lastRunStatus;
	
	private String lastRunFailureMessage;
	
	private String notificationEmail;
	
	private String seconds;
	
	private String minutes;
	
	private String hours;
	
	private String dayOfMonth;
	
	private String month;
	
	private String dayOfWeek;
	
	private String year;
	
	@EmbedMany
	private Set<RunHistory> runHistories;
	
	public ScheduledJob() {
		
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
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

	public String getEnvironmentKey() {
		return environmentKey;
	}

	public void setEnvironmentKey(String environmentKey) {
		this.environmentKey = environmentKey;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public Boolean getIsSandbox() {
		return isSandbox;
	}

	public void setIsSandbox(Boolean isSandbox) {
		this.isSandbox = isSandbox;
	}

	public String getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(String connectorId) {
		this.connectorId = connectorId;
	}

	public JobType getJobType() {
		return jobType;
	}

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastRunDate() {
		return lastRunDate;
	}

	public void setLastRunDate(Date lastRunDate) {
		this.lastRunDate = lastRunDate;
	}

	public String getLastRunStatus() {
		return lastRunStatus;
	}

	public void setLastRunStatus(String lastRunStatus) {
		this.lastRunStatus = lastRunStatus;
	}

	public String getLastRunFailureMessage() {
		return lastRunFailureMessage;
	}

	public void setLastRunFailureMessage(String lastRunFailureMessage) {
		this.lastRunFailureMessage = lastRunFailureMessage;
	}

	public String getNotificationEmail() {
		return notificationEmail;
	}

	public void setNotificationEmail(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}

	public Set<RunHistory> getRunHistories() {
		return runHistories;
	}

	public void setRunHistories(Set<RunHistory> runHistories) {
		this.runHistories = runHistories;
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
}