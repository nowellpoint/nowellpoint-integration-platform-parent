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

package com.nowellpoint.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Job extends AbstractResource {
	
	private Source source;
	
	private UserInfo owner;
	
	private String description;
	
	private String notificationEmail;
	
	private String slackWebhookUrl;
	
	private String scheduleOption;
	
	private Schedule schedule;
	
	private String jobName;
	
	private Long jobRunTime;
	
	private Date fireTime;
	
	private Date nextFireTime;
	
	private String status;
	
	private String failureMessage;
	
	private Integer numberOfExecutions;
	
	private List<JobExecution> jobExecutions;
	
	private List<JobOutput> jobOutputs;

	public Job() {
		jobExecutions = new ArrayList<>();
		jobOutputs = new ArrayList<>();
	}

	public Source getSource() {
		return source;
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

	public String getNotificationEmail() {
		return notificationEmail;
	}

	public String getSlackWebhookUrl() {
		return slackWebhookUrl;
	}

	public String getScheduleOption() {
		return scheduleOption;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public String getJobName() {
		return jobName;
	}

	public Long getJobRunTime() {
		return jobRunTime;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public Date getFireTime() {
		return fireTime;
	}

	public String getStatus() {
		return status;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public Integer getNumberOfExecutions() {
		return numberOfExecutions;
	}

	public List<JobExecution> getJobExecutions() {
		return jobExecutions;
	}
	
	public List<JobOutput> getJobOutputs() {
		return jobOutputs;
	}
}