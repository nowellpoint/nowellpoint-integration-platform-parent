package com.nowellpoint.api.rest.service;

import static com.nowellpoint.util.Assert.isEmpty;
import static com.nowellpoint.util.Assert.isNull;
import static com.nowellpoint.util.Assert.isNullOrEmpty;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.ValidationException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.Backup;
import com.nowellpoint.api.rest.domain.Deactivate;
import com.nowellpoint.api.rest.domain.Instance;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobStatus;
import com.nowellpoint.api.rest.domain.JobType;
import com.nowellpoint.api.rest.domain.RunHistory;
import com.nowellpoint.api.rest.domain.SalesforceConnector;
import com.nowellpoint.api.rest.domain.ScheduledJob;
import com.nowellpoint.api.rest.domain.ScheduledJobList;
import com.nowellpoint.api.rest.domain.ScheduledJobType;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.api.service.ScheduledJobService;
import com.nowellpoint.api.service.ScheduledJobTypeService;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Assert;

public class ScheduledJobServiceImpl extends AbstractScheduledJobService implements ScheduledJobService {
	
	private static final String BUCKET_NAME = "nowellpoint-metadata-backups";
	
	@Inject
	private ScheduledJobTypeService scheduledJobTypeService;
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	public ScheduledJobServiceImpl() {
		
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public ScheduledJobList findByOwner(String ownerId) {
		return super.findAllByOwner(ownerId);
	}
	
	/**
	 * 
	 * 
	 * 
	 */
	
	@Override
	public ScheduledJobList findScheduled() {
		return super.findByStatus("Scheduled");
	}
	
	@Override
	public ScheduledJob createScheduledJob(String scheduledJobTypeId) {
		ScheduledJobType scheduledJobType = scheduledJobTypeService.findById(scheduledJobTypeId);
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		Date now = Date.from(Instant.now());
		
		ScheduledJob scheduledJob = new ScheduledJob();
		scheduledJob.setStatus(JobStatus.NOT_SCHEDULED);
		scheduledJob.setOwner(userInfo);
		scheduledJob.setCreatedOn(now);
		scheduledJob.setCreatedBy(userInfo);
		scheduledJob.setLastUpdatedOn(now);
		scheduledJob.setLastUpdatedBy(userInfo);
		
		JobType jobType = new JobType();
		jobType.setCode(scheduledJobType.getCode());
		jobType.setConnectorType(scheduledJobType.getConnectorType());
		jobType.setDescription(scheduledJobType.getDescription());
		jobType.setId(scheduledJobType.getId());
		jobType.setName(scheduledJobType.getName());
		
		scheduledJob.setJobType(jobType);
		
		create(scheduledJob);
		
		return scheduledJob;
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param scheduledJob
	 * 
	 * 
	 */
	
	@Override
	public void updateScheduledJob(String id, ScheduledJob scheduledJob) {
		
		/**
		 * retrieve the original record
		 */
		
		ScheduledJob original = findById(id);
		
		/**
		 * validation steps
		 */
		
		if (JobStatus.TERMINATED.equals(original.getStatus())) {
			throw new ValidationException( "Scheduled Job has been terminated and cannot be altered" );
		}
		
		if (scheduledJob.getScheduleDate().before(Date.from(Instant.now()))) {
			throw new ValidationException( "Schedule Date cannot be before current date" );
		}
		
		if (! (JobStatus.SCHEDULED.equals(scheduledJob.getStatus()) || JobStatus.STOPPED.equals(scheduledJob.getStatus())) || JobStatus.TERMINATED.equals(scheduledJob.getStatus())) {
			throw new ValidationException( String.format( "Invalid status: %s", scheduledJob.getStatus() ) );
		}
		
		/**
		 * add read-only fields
		 */
		
		scheduledJob.setId(id);
		scheduledJob.setStatus(original.getStatus());
		scheduledJob.setJobType(original.getJobType());
		scheduledJob.setCreatedOn(original.getCreatedOn());
		scheduledJob.setLastRunDate(original.getLastRunDate());
		scheduledJob.setLastRunStatus(original.getLastRunStatus());
		scheduledJob.setLastRunFailureMessage(original.getLastRunFailureMessage());
		scheduledJob.setCreatedBy(original.getCreatedBy());
		
		/**
		 * ensure a complete record for fields that can be updated
		 */
		
		if (isNull(scheduledJob.getDescription())) {
			scheduledJob.setDescription(original.getDescription());
		} else if (isEmpty(scheduledJob.getDescription())) {
			scheduledJob.setDescription(null);
		}

		if (isNull(scheduledJob.getConnectorId())) {
			scheduledJob.setConnectorId(original.getConnectorId());
		}

		if (isNull(scheduledJob.getOwner())) {
			scheduledJob.setOwner(original.getOwner());
		}
		
		if (isNull(scheduledJob.getScheduleDate())) {
			scheduledJob.setScheduleDate(original.getScheduleDate());
		}
		
		if (isNull(scheduledJob.getStatus())) {
			scheduledJob.setStatus(original.getStatus());
		}
		
		if (isNullOrEmpty(scheduledJob.getNotificationEmail())) {
			scheduledJob.setNotificationEmail(original.getNotificationEmail());
		}
		
		if (isNull(scheduledJob.getRunHistories())) {
			scheduledJob.setRunHistories(original.getRunHistories());
		}

		/**
		 * add type specific elements
		 */
		
		if ("SALESFORCE".equals(scheduledJob.getJobType().getConnectorType().getCode())) {
			
			SalesforceConnector salesforceConnector = null;
			try {
				salesforceConnector = salesforceConnectorService.findById( scheduledJob.getConnectorId() );
			} catch (DocumentNotFoundException e) {
				throw new ValidationException(String.format("Invalid Connector Id: %s for SalesforceConnector", scheduledJob.getConnectorId()));
			}

			Optional<Instance> instance = null;
			if (scheduledJob.getEnvironmentKey() != null && ! scheduledJob.getEnvironmentKey().trim().isEmpty()) {
				instance = salesforceConnector.getInstances()
						.stream()
						.filter(e -> scheduledJob.getEnvironmentKey().equals(e.getKey()))
						.findFirst();
				
				if (! instance.isPresent()) {
					throw new ValidationException(String.format("Invalid environment key: %s", scheduledJob.getEnvironmentKey()));
				}
				
			} else {
				instance = salesforceConnector.getInstances().stream().filter(e -> ! e.getIsSandbox()).findFirst();
			}

			if (scheduledJob.getNotificationEmail() == null) {
				scheduledJob.setNotificationEmail(instance.get().getEmail());
			}
			
			scheduledJob.setIsSandbox(instance.get().getIsSandbox());
			scheduledJob.setEnvironmentKey(instance.get().getKey());
			scheduledJob.setEnvironmentName(instance.get().getEnvironmentName());
		}
		
		/**
		 * add audit fields
		 */
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		scheduledJob.setLastUpdatedOn(now);
		scheduledJob.setLastUpdatedBy(userInfo);
		
		/**
		 * perform update
		 */
		
		update(scheduledJob);
		
		/**
		 * submit the scheduled job to the job operator
		 */
		
		submitJob(scheduledJob);
	}
	
	/**
	 * 
	 * @param id
	 */
	
	@Override
	public ScheduledJob terminateScheduledJob(String id) {
		ScheduledJob scheduledJob = findById(id);
		scheduledJob.setStatus(JobStatus.TERMINATED);
		update(scheduledJob);
		return scheduledJob;
	}
	
	/**
	 * 
	 * @param id
	 */
	
	@Override
	public ScheduledJob startScheduledJob(String id) {
		ScheduledJob scheduledJob = findById(id);
		scheduledJob.setStatus(JobStatus.SCHEDULED);
		update(scheduledJob);
		return scheduledJob;
	}
	
	/**
	 * 
	 * @param id
	 */
	
	@Override
	public ScheduledJob stopScheduledJob(String id) {
		ScheduledJob scheduledJob = findById(id);
		scheduledJob.setStatus(JobStatus.STOPPED);
		update(scheduledJob);
		return scheduledJob;
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * 
	 * 
	 */
	
	@Override
	public void deleteScheduledJob(String id) {
		ScheduledJob resource = findById(id);
		delete(resource);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */

	@Override
	public ScheduledJob findById(String id) {
		return super.findById(id);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param fireInstanceId
	 * @return
	 * 
	 * 
	 */
	
	@Override
	public RunHistory findRunHistory(String id, String fireInstanceId) {
		ScheduledJob scheduledJob = findById( id );
		
		Optional<RunHistory> filter = scheduledJob.getRunHistories()
				.stream()
				.filter(r -> r.getFireInstanceId().equals(fireInstanceId))
				.findFirst();
		
		return filter.get();
	}
	
	/**
	 * 
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 * 
	 * 
	 */
	
	@Override
	public String getFile(String id, String fireInstanceId, String filename) throws IOException {
		RunHistory runHistory = findRunHistory(id, fireInstanceId);
		
		Backup backup = null;
		
		if (Assert.isNotNull(runHistory)) {
			Optional<Backup> filter = runHistory.getBackups()
					.stream()
					.filter(r -> r.getType().equals(filename))
					.findFirst();
			
			if (filter.isPresent()) {
				backup = filter.get();
			}
		}
		
		AmazonS3 s3client = new AmazonS3Client();
		
		GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET_NAME, backup.getFilename());
    	
    	S3Object s3Object = s3client.getObject(getObjectRequest);
    	
    	return IOUtils.toString(s3Object.getObjectContent());
	}
	
	/**
	 * 
	 * 
	 * 
	 */
	
	@Override
	public void terminateAllJobs(@Observes @Deactivate AccountProfile accountProfile) {
		ScheduledJobList list = findByOwner(accountProfile.getId());
		list.getItems().stream().forEach(scheduledJob -> {
			scheduledJob.setStatus(JobStatus.TERMINATED);
			update(scheduledJob);
		});
	}
	
	private void submitJob(ScheduledJob scheduledJob) {
		Date now = Date.from(Instant.now());
		
		Job job = new Job();
		job.setCreatedOn(now);
		job.setDayOfMonth(scheduledJob.getDayOfMonth());
		job.setDayOfWeek(scheduledJob.getDayOfWeek());
		job.setHours(scheduledJob.getHours());
		job.setJobName(scheduledJob.getJobType().getCode());
		job.setLastUpdatedOn(now);
		job.setMinutes(scheduledJob.getMinutes());
		job.setMonth(scheduledJob.getMonth());
		job.setSeconds(scheduledJob.getSeconds());
		job.setStatus(JobStatus.SCHEDULED);
		job.setYear(scheduledJob.getYear());
		
		submit(job);
	}
}