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

package com.nowellpoint.api.rest.service;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.ValidationException;

import org.jboss.logging.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.mongodb.client.model.Filters;
import com.nowellpoint.annotation.Stop;
import com.nowellpoint.annotation.Submit;
import com.nowellpoint.annotation.Terminate;
import com.nowellpoint.api.rest.domain.CreateJobRequest;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobExecution;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.rest.domain.JobOutput;
import com.nowellpoint.api.rest.domain.UpdateJobRequest;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.CommunicationService;
import com.nowellpoint.api.service.JobService;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.util.Assert;

public class JobServiceImpl extends AbstractJobService implements JobService {
	
	private static final Logger LOGGER = Logger.getLogger(JobServiceImpl.class);
	
	protected final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
	
	@Inject
	@Submit
	private Event<Job> submitJob;
	
	@Inject
	@Stop
	private Event<Job> stopJob;
	
	@Inject
	@Terminate
	private Event<Job> terminateJob;
	
	@Inject
	private CommunicationService communicationService;

	@Override
	public JobList findAllByOwner(String ownerId) {
		return super.findAllByOwner(ownerId);
	}
	
	@Override
	public JobList findAllScheduled() {
		return super.findAllScheduled();
	}

	@Override
	public Job findById(String id) {
		return super.findById(id);
	}
	
	@Override
	public JobExecution findByFireInstanceId(String id, String fireInstanceId) {
		Job job = findById(id);
		return job.getJobExecution(fireInstanceId);
	}
	
	@Override
	public JobList queryBySource(String sourceId) {
		return super.query( Filters.and( 
				Filters.eq ( "source.id", sourceId ), 
				Filters.eq( "source.type", "SALESFORCE" ) ) );
	}
	
	@Override
	public void stopJob(Job job) {
		job.setStatus(Job.Statuses.STOPPED);
		update(job);
		stopJob.fire(job);
	}
	
	@Override
	public void terminateJob(Job job) {
		job.setStatus(Job.Statuses.TERMINATED);
		update(job);
		terminateJob.fire(job);
	}
	
	@Override
	public String getOutputFile(String id, String filename) throws IOException {
		Job job = findById(id);
		JobOutput jobOutput = job.getJobOutput(filename);
		
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		GetObjectRequest getObjectRequest = new GetObjectRequest(jobOutput.getBucket(), jobOutput.getKey());
    	
    	S3Object s3Object = s3client.getObject(getObjectRequest);
    	
    	return IOUtils.toString(s3Object.getObjectContent());
	}
	
	@Override
	public Job createJob(@Observes CreateJobRequest jobRequest) {
		
		List<String> errors = new ArrayList<>();
		
		if (Assert.isNull(jobRequest.getSource())) {
			errors.add("Missing Source. Create Job must include a Source system");
		}
		
		if (Assert.isNull(jobRequest.getJobType())) {
			errors.add("Missing Job Type. Create Job must include a Job Type");
		}
		
		if (Assert.isNull(jobRequest.getSchedule()) && ! jobRequest.getScheduleOption().isPresent()) {
			errors.add("Missing scheduleOption parameter. Must provide a value of RUN_WHEN_SUBMITTED, RUN_ONCE, RUN_ON_SCHEDULE or RUN_ON_SPECIFIC_DAYS");
		}
		
		if (! errors.isEmpty()) {
			String errorMessage = errors.stream().collect(Collectors.joining ("\n"));
			throw new ValidationException(errorMessage);
		}
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName());

		Job job = Job.of(jobRequest, userInfo);
		
		createJob(job);
		
		fireSubmitJobEvent(job);
		
		return job;
	}

	@Override
	public Job updateJob(@Observes UpdateJobRequest jobRequest) {
		
		Job job = findById(jobRequest.getId());
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName());
		
		job.setLastUpdatedBy(userInfo);
		job.setLastUpdatedOn(Date.from(Instant.now()));
		job.setDescription(jobRequest.getDescription().orElse(null));
		job.setNotificationEmail(jobRequest.getNotificationEmail().orElse(null));
		job.setSlackWebhookUrl(jobRequest.getSlackWebhookUrl().orElse(null));
		
		updateJob(job);
		
		return job;
	}
	
	private void createJob(Job job) {
		super.create(job);
	}
	
	@Override
	public void updateJob(Job job) {
		super.update(job);
	}
	
	@Override
	public void submitJob(Job job) {
		if (job.getStatus().equals(Job.Statuses.TERMINATED) || job.getStatus().equals(Job.Statuses.SCHEDULED)) {
			throw new ValidationException(MessageProvider.getMessage(Locale.US, MessageConstants.JOB_UNABLE_TO_SUBMIT));
		}		
		fireSubmitJobEvent(job);
	}
	
	@Override
	public void loadScheduledJobs() {
		JobList jobList = findAllScheduled();
		
		jobList.getItems().stream().forEach(job -> {
			fireSubmitJobEvent(job);
		});
	}
	
	@Override
	public void sendSlackTestMessage(Job job) {
		String subject = MessageProvider.getMessage(Locale.US, MessageConstants.SLACK_MESSAGE_SUBJECT);
		String testMessage = MessageProvider.getMessage(Locale.US, MessageConstants.SLACK_TEST_MESSAGE);
		communicationService.sendMessage(job.getSlackWebhookUrl(), subject, testMessage);
	}
	
	private void fireSubmitJobEvent(Job job) {
		LOGGER.info(String.format("Submitting Job: %s", job.getId()));
		submitJob.fire(job);
	}
}