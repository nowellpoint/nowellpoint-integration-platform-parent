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

package com.nowellpoint.api.service.impl;

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
import com.nowellpoint.api.annotation.Stop;
import com.nowellpoint.api.annotation.Submit;
import com.nowellpoint.api.annotation.Terminate;
import com.nowellpoint.api.rest.domain.CreateJobRequest;
import com.nowellpoint.api.rest.domain.JobOrig;
import com.nowellpoint.api.rest.domain.JobExecution;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.rest.domain.JobOutput;
import com.nowellpoint.api.rest.domain.JobScheduleOptions;
import com.nowellpoint.api.rest.domain.JobStatus;
import com.nowellpoint.api.rest.domain.UpdateJobRequest;
import com.nowellpoint.api.rest.domain.AbstractUserInfo;
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
	private Event<JobOrig> submitJob;
	
	@Inject
	@Stop
	private Event<JobOrig> stopJob;
	
	@Inject
	@Terminate
	private Event<JobOrig> terminateJob;
	
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
	public JobOrig findById(String id) {
		return super.findById(id);
	}
	
	@Override
	public JobExecution findByFireInstanceId(String id, String fireInstanceId) {
		JobOrig jobOrig = findById(id);
		return jobOrig.getJobExecution(fireInstanceId);
	}
	
	@Override
	public JobList queryBySource(String sourceId) {
		return super.query( Filters.and( 
				Filters.eq ( "source.id", sourceId ), 
				Filters.eq( "source.type", "SALESFORCE" ) ) );
	}
	
	@Override
	public void submitJob(JobOrig jobOrig) {
		if (jobOrig.getStatus().equals(JobStatus.TERMINATED) || jobOrig.getStatus().equals(JobStatus.SCHEDULED)) {
			throw new ValidationException(MessageProvider.getMessage(Locale.US, MessageConstants.JOB_UNABLE_TO_SUBMIT));
		}
		
		fireSubmitJobEvent(jobOrig);
		
		if (jobOrig.getSchedule().getRunAt().after(Date.from(Instant.now()))) {
			jobOrig.setStatus(JobStatus.SCHEDULED);
		} else {
			jobOrig.setStatus(JobStatus.SUBMITTED);
		}
		
		updateJob(jobOrig);
	}
	
	@Override
	public void runJob(JobOrig jobOrig) {
		jobOrig.setScheduleOption(JobScheduleOptions.RUN_WHEN_SUBMITTED);
		fireSubmitJobEvent(jobOrig);
	}
	
	@Override
	public void stopJob(JobOrig jobOrig) {
		stopJob.fire(jobOrig);
		jobOrig.setStatus(JobStatus.STOPPED);
		updateJob(jobOrig);
	}
	
	@Override
	public void terminateJob(JobOrig jobOrig) {
		terminateJob.fire(jobOrig);
		jobOrig.setStatus(JobStatus.TERMINATED);
		updateJob(jobOrig);
	}
	
	@Override
	public String getOutputFile(String id, String filename) throws IOException {
		JobOrig jobOrig = findById(id);
		JobOutput jobOutput = jobOrig.getJobOutput(filename);
		
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		GetObjectRequest getObjectRequest = new GetObjectRequest(jobOutput.getBucket(), jobOutput.getKey());
    	
    	S3Object s3Object = s3client.getObject(getObjectRequest);
    	
    	return IOUtils.toString(s3Object.getObjectContent());
	}
	
	@Override
	public JobOrig createJob(@Observes CreateJobRequest jobRequest) {
		
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
		
		AbstractUserInfo abstractUserInfo = AbstractUserInfo.of(UserContext.getPrincipal().getName());

		JobOrig jobOrig = JobOrig.of(jobRequest, abstractUserInfo);
		
		createJob(jobOrig);
		
		fireSubmitJobEvent(jobOrig);
		
		return jobOrig;
	}

	@Override
	public JobOrig updateJob(@Observes UpdateJobRequest jobRequest) {
		
		JobOrig jobOrig = findById(jobRequest.getId());
		
		AbstractUserInfo abstractUserInfo = AbstractUserInfo.of(UserContext.getPrincipal().getName());
		
		jobOrig.setLastUpdatedBy(abstractUserInfo);
		jobOrig.setLastUpdatedOn(Date.from(Instant.now()));
		jobOrig.setDescription(jobRequest.getDescription().orElse(null));
		jobOrig.setNotificationEmail(jobRequest.getNotificationEmail().orElse(null));
		jobOrig.setSlackWebhookUrl(jobRequest.getSlackWebhookUrl().orElse(null));
		
		updateJob(jobOrig);
		
		return jobOrig;
	}
	
	private void createJob(JobOrig jobOrig) {
		super.create(jobOrig);
	}
	
	@Override
	public void updateJob(JobOrig jobOrig) {
		super.update(jobOrig);
	}
	
	@Override
	public void loadScheduledJobs() {
		JobList jobList = findAllScheduled();
		
		jobList.getItems().stream().forEach(job -> {
			fireSubmitJobEvent(job);
		});
	}
	
	@Override
	public void sendSlackTestMessage(JobOrig jobOrig) {
		String subject = MessageProvider.getMessage(Locale.US, MessageConstants.SLACK_MESSAGE_SUBJECT);
		String testMessage = MessageProvider.getMessage(Locale.US, MessageConstants.SLACK_TEST_MESSAGE);
		communicationService.sendMessage(jobOrig.getSlackWebhookUrl(), subject, testMessage);
	}
	
	private void fireSubmitJobEvent(JobOrig jobOrig) {
		LOGGER.info(String.format("Submitting Job: %s", jobOrig.getId()));
		submitJob.fire(jobOrig);
	}
}