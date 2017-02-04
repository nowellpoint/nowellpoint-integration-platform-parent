package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.nowellpoint.util.Assert.isEmpty;
import static com.nowellpoint.util.Assert.isNull;
import static com.nowellpoint.util.Assert.isNullOrEmpty;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.ValidationException;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.nowellpoint.api.model.document.ScheduledJobRequest;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.Backup;
import com.nowellpoint.api.rest.domain.Deactivate;
import com.nowellpoint.api.rest.domain.Instance;
import com.nowellpoint.api.rest.domain.RunHistory;
import com.nowellpoint.api.rest.domain.SalesforceConnector;
import com.nowellpoint.api.rest.domain.ScheduledJob;
import com.nowellpoint.api.rest.domain.ScheduledJobList;
import com.nowellpoint.api.rest.domain.ScheduledJobStatus;
import com.nowellpoint.api.rest.domain.ScheduledJobType;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.api.service.ScheduledJobService;
import com.nowellpoint.api.service.ScheduledJobTypeService;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;

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
	
	/**
	 * 
	 * 
	 * 
	 */
	
	@Override
	public void createScheduledJob(ScheduledJob scheduledJob) {
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		if (isNull(scheduledJob.getOwner())) {
			scheduledJob.setOwner(userInfo);
		}
		
		scheduledJob.setStatus(ScheduledJobStatus.SCHEDULED);
		
		setupScheduledJob(scheduledJob);
		
		Date now = Date.from(Instant.now());
		
		scheduledJob.setCreatedOn(now);
		scheduledJob.setCreatedBy(userInfo);
		scheduledJob.setLastUpdatedOn(now);
		scheduledJob.setLastModifiedBy(userInfo);
		
		create(scheduledJob);
		
		submitScheduledJobRequest(scheduledJob);
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
		ScheduledJob original = findById(id);
		
		if (ScheduledJobStatus.TERMINATED.equals(original.getStatus())) {
			throw new ValidationException( "Scheduled Job has been terminated and cannot be altered" );
		}
		
		scheduledJob.setId(id);
		scheduledJob.setStatus(original.getStatus());
		scheduledJob.setJobTypeId(original.getJobTypeId());
		scheduledJob.setJobTypeCode(original.getJobTypeCode());
		scheduledJob.setJobTypeName(original.getJobTypeName());
		scheduledJob.setCreatedOn(original.getCreatedOn());
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
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		scheduledJob.setLastUpdatedOn(now);
		scheduledJob.setLastModifiedBy(userInfo);
		
		update(scheduledJob);
		
		submitScheduledJobRequest(scheduledJob);
	}
	
	/**
	 * 
	 * @param id
	 */
	
	@Override
	public ScheduledJob terminateScheduledJob(String id) {
		ScheduledJob scheduledJob = findById(id);
		scheduledJob.setStatus(ScheduledJobStatus.TERMINATED);
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
		scheduledJob.setStatus(ScheduledJobStatus.SCHEDULED);
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
		scheduledJob.setStatus(ScheduledJobStatus.STOPPED);
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
			scheduledJob.setStatus(ScheduledJobStatus.TERMINATED);
			update(scheduledJob);
		});
	}
	
	/**
	 * 
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
		
		ScheduledJobType scheduledJobType = scheduledJobTypeService.findById( scheduledJob.getJobTypeId() );

		if ("SALESFORCE".equals(scheduledJobType.getConnectorType().getCode())) {
			
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

		scheduledJob.setConnectorType(scheduledJobType.getConnectorType().getCode());
		scheduledJob.setJobTypeCode(scheduledJobType.getCode());
		scheduledJob.setJobTypeName(scheduledJobType.getName());

	}
	
	private void submitScheduledJobRequest(ScheduledJob scheduledJob) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC"));
		Date now = Date.from(Instant.now());
		
		ObjectId id = new ObjectId( System.getProperty( Properties.DEFAULT_SUBJECT ) );

		ScheduledJobRequest scheduledJobRequest = new ScheduledJobRequest();
		scheduledJobRequest.setScheduledJobId(new ObjectId(scheduledJob.getId()));
		scheduledJobRequest.setConnectorId(scheduledJob.getConnectorId());
		scheduledJobRequest.setConnectorType(scheduledJob.getConnectorType());
		scheduledJobRequest.setOwner( documentManager.getReference(com.nowellpoint.api.model.document.UserInfo.class, id) );
		scheduledJobRequest.setCreatedOn(now);
		scheduledJobRequest.setCreatedBy( documentManager.getReference(com.nowellpoint.api.model.document.UserInfo.class, id) );
		scheduledJobRequest.setDescription(scheduledJob.getDescription());
		scheduledJobRequest.setEnvironmentKey(scheduledJob.getEnvironmentKey());
		scheduledJobRequest.setEnvironmentName(scheduledJob.getEnvironmentName());
		scheduledJobRequest.setIsSandbox(scheduledJob.getIsSandbox());
		scheduledJobRequest.setJobTypeCode(scheduledJob.getJobTypeCode());
		scheduledJobRequest.setJobTypeId(scheduledJob.getJobTypeCode());
		scheduledJobRequest.setJobTypeName(scheduledJob.getJobTypeName());
		scheduledJobRequest.setLastUpdatedOn(now);
		scheduledJobRequest.setLastUpdatedBy( documentManager.getReference(com.nowellpoint.api.model.document.UserInfo.class, id) );
		scheduledJobRequest.setNotificationEmail(scheduledJob.getNotificationEmail());
		scheduledJobRequest.setScheduleDate(scheduledJob.getScheduleDate());
		scheduledJobRequest.setStatus(scheduledJob.getStatus());
		scheduledJobRequest.setYear(dateTime.getYear());
		scheduledJobRequest.setMonth(dateTime.getMonth().getValue());
		scheduledJobRequest.setDay(dateTime.getDayOfMonth());
		scheduledJobRequest.setHour(dateTime.getHour());
		scheduledJobRequest.setMinute(dateTime.getMinute());
		scheduledJobRequest.setSecond(dateTime.getSecond());
		
		Bson query = and ( 
				eq ( "scheduledJobId", scheduledJob.getId().toString() ), 
				or ( eq ( "status", "Scheduled" ), eq ( "status", "Stopped" ))); 
		
		documentManager.upsert( query , scheduledJobRequest );
	}
}