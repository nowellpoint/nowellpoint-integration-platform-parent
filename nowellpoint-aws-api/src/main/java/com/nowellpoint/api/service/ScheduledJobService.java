package com.nowellpoint.api.service;

import static com.nowellpoint.util.Assert.isEmpty;
import static com.nowellpoint.util.Assert.isNull;
import static com.nowellpoint.util.Assert.isNullOrEmpty;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.ValidationException;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.api.model.domain.Backup;
import com.nowellpoint.api.model.domain.Deactivate;
import com.nowellpoint.api.model.domain.Environment;
import com.nowellpoint.api.model.domain.RunHistory;
import com.nowellpoint.api.model.domain.SalesforceConnector;
import com.nowellpoint.api.model.domain.ScheduledJob;
import com.nowellpoint.api.model.domain.ScheduledJobStatus;
import com.nowellpoint.api.model.domain.ScheduledJobType;
import com.nowellpoint.api.model.domain.UserInfo;
import com.nowellpoint.api.model.mapper.ScheduledJobModelMapper;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Assert;

public class ScheduledJobService extends ScheduledJobModelMapper {
	
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
	
	public ScheduledJobService() {
		
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	public Set<ScheduledJob> findByOwner() {
		return super.findByOwner();
	}
	
	/**
	 * 
	 * 
	 * @param scheduledJob
	 * 
	 * 
	 */
	
	public void createScheduledJob(ScheduledJob scheduledJob) {
		if (isNull(scheduledJob.getOwner())) {
			scheduledJob.setOwner(new UserInfo(getSubject()));
		}
		
		scheduledJob.setStatus(ScheduledJobStatus.SCHEDULED);
		
		setupScheduledJob(scheduledJob);
		
		super.createScheduledJob(scheduledJob);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param scheduledJob
	 * 
	 * 
	 */
	
	public void updateScheduledJob(String id, ScheduledJob scheduledJob) {
		ScheduledJob original = findScheduledJobById(id);
		
		if (ScheduledJobStatus.TERMINATED.equals(original.getStatus())) {
			throw new ValidationException( "Scheduled Job has been terminated and cannot be altered" );
		}
		
		scheduledJob.setId(id);
		scheduledJob.setJobTypeId(original.getJobTypeId());
		scheduledJob.setJobTypeCode(original.getJobTypeCode());
		scheduledJob.setJobTypeName(original.getJobTypeName());
		scheduledJob.setCreatedDate(original.getCreatedDate());
		scheduledJob.setSystemCreatedDate(original.getSystemCreatedDate());
		scheduledJob.setLastRunDate(original.getLastRunDate());
		scheduledJob.setLastRunStatus(original.getLastRunStatus());
		scheduledJob.setLastRunFailureMessage(original.getLastRunFailureMessage());
		scheduledJob.setCreatedBy(original.getCreatedBy());
		
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
		
		setupScheduledJob(scheduledJob);
		
		super.updateScheduledJob(scheduledJob);
	}
	
	/**
	 * 
	 * @param id
	 */
	
	public ScheduledJob terminateScheduledJob(String id) {
		ScheduledJob scheduledJob = findScheduledJobById(id);
		scheduledJob.setStatus(ScheduledJobStatus.TERMINATED);
		updateScheduledJob(scheduledJob);
		return scheduledJob;
	}
	
	/**
	 * 
	 * @param id
	 */
	
	public ScheduledJob startScheduledJob(String id) {
		ScheduledJob scheduledJob = findScheduledJobById(id);
		scheduledJob.setStatus(ScheduledJobStatus.SCHEDULED);
		updateScheduledJob(scheduledJob);
		return scheduledJob;
	}
	
	/**
	 * 
	 * @param id
	 */
	
	public ScheduledJob stopScheduledJob(String id) {
		ScheduledJob scheduledJob = findScheduledJobById(id);
		scheduledJob.setStatus(ScheduledJobStatus.STOPPED);
		updateScheduledJob(scheduledJob);
		return scheduledJob;
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * 
	 * 
	 */
	
	public void deleteScheduledJob(String id) {
		super.deleteScheduledJob(findScheduledJobById(id));
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */

	public ScheduledJob findScheduledJobById(String id) {
		return super.findScheduedJobById(id);
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
	
	public RunHistory findRunHistory(String id, String fireInstanceId) {
		ScheduledJob scheduledJob = findScheduledJobById( id );
		
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
	 * @param accountProfile
	 * 
	 * 
	 */
	
	public void terminateAllJobs(@Observes @Deactivate AccountProfile accountProfile) {
		Set<ScheduledJob> scheduledJobs = findByOwner(accountProfile.getId());
		scheduledJobs.stream().forEach(scheduledJob -> {
			scheduledJob.setStatus(ScheduledJobStatus.TERMINATED);
			updateScheduledJob(scheduledJob);
		});
	}
	
	/**
	 * 
	 * 
	 * @param scheduledJob
	 * 
	 * 
	 */
	
	private void setupScheduledJob(ScheduledJob scheduledJob) {
		
		if (scheduledJob.getScheduleDate().before(Date.from(Instant.now()))) {
			throw new ValidationException( "Schedule Date cannot be before current date" );
		}
		
		if (! (ScheduledJobStatus.SCHEDULED.equals(scheduledJob.getStatus()) || ScheduledJobStatus.STOPPED.equals(scheduledJob.getStatus())) || ScheduledJobStatus.TERMINATED.equals(scheduledJob.getStatus())) {
			throw new ValidationException( String.format( "Invalid status: %s", scheduledJob.getStatus() ) );
		}
		
		ScheduledJobType scheduledJobType = scheduledJobTypeService.findScheduedJobTypeById( scheduledJob.getJobTypeId() );

		if ("SALESFORCE".equals(scheduledJobType.getConnectorType().getCode())) {
			
			SalesforceConnector salesforceConnector = null;
			try {
				salesforceConnector = salesforceConnectorService.findSalesforceConnector( scheduledJob.getConnectorId() );
			} catch (DocumentNotFoundException e) {
				throw new ValidationException(String.format("Invalid Connector Id: %s for SalesforceConnector", scheduledJob.getConnectorId()));
			}

			Optional<Environment> environment = null;
			if (scheduledJob.getEnvironmentKey() != null && ! scheduledJob.getEnvironmentKey().trim().isEmpty()) {
				environment = salesforceConnector.getEnvironments()
						.stream()
						.filter(e -> scheduledJob.getEnvironmentKey().equals(e.getKey()))
						.findFirst();
				
				if (! environment.isPresent()) {
					throw new ValidationException(String.format("Invalid environment key: %s", scheduledJob.getEnvironmentKey()));
				}
				
			} else {
				environment = salesforceConnector.getEnvironments().stream().filter(e -> ! e.getIsSandbox()).findFirst();
			}

			if (scheduledJob.getNotificationEmail() == null) {
				scheduledJob.setNotificationEmail(environment.get().getEmail());
			}
			
			scheduledJob.setIsSandbox(environment.get().getIsSandbox());
			scheduledJob.setEnvironmentKey(environment.get().getKey());
			scheduledJob.setEnvironmentName(environment.get().getEnvironmentName());
		}

		scheduledJob.setConnectorType(scheduledJobType.getConnectorType().getCode());
		scheduledJob.setJobTypeCode(scheduledJobType.getCode());
		scheduledJob.setJobTypeName(scheduledJobType.getName());

	}
}