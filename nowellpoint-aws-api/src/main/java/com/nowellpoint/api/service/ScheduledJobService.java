package com.nowellpoint.api.service;

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

import org.bson.types.ObjectId;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.mongodb.DBRef;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.UpdateOptions;
import com.nowellpoint.api.model.document.ScheduledJobRequest;
import com.nowellpoint.api.model.document.UserRef;
import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.api.model.domain.Backup;
import com.nowellpoint.api.model.domain.Deactivate;
import com.nowellpoint.api.model.domain.Instance;
import com.nowellpoint.api.model.domain.RunHistory;
import com.nowellpoint.api.model.domain.SalesforceConnector;
import com.nowellpoint.api.model.domain.ScheduledJob;
import com.nowellpoint.api.model.domain.ScheduledJobList;
import com.nowellpoint.api.model.domain.ScheduledJobStatus;
import com.nowellpoint.api.model.domain.ScheduledJobType;
import com.nowellpoint.api.model.domain.UserInfo;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.mongodb.document.MongoDatastore;
import com.nowellpoint.mongodb.document.MongoDocumentService;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;

public class ScheduledJobService {
	
	private MongoDocumentService mongoDocumentService = new MongoDocumentService();
	
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
	
	public ScheduledJobList findByOwner(String ownerId) {
		
		FindIterable<com.nowellpoint.api.model.document.ScheduledJob> documents = mongoDocumentService.find(com.nowellpoint.api.model.document.ScheduledJob.class, 
				eq ( "owner.identity", new DBRef( MongoDatastore.getCollectionName( com.nowellpoint.api.model.document.AccountProfile.class ), new ObjectId( ownerId ) ) ) );
		
		ScheduledJobList resources = new ScheduledJobList(documents);
		
		return resources;
	}
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public ScheduledJobList findScheduled() {
		
		FindIterable<com.nowellpoint.api.model.document.ScheduledJob> documents = mongoDocumentService.find(com.nowellpoint.api.model.document.ScheduledJob.class, 
				eq ( "status", "Scheduled" ) );
		
		ScheduledJobList resources = new ScheduledJobList(documents);
		
		return resources;
	}
	
	/**
	 * 
	 * 
	 * 
	 */
	
	public void createScheduledJob(ScheduledJob scheduledJob) {
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		if (isNull(scheduledJob.getOwner())) {
			scheduledJob.setOwner(userInfo);
		}
		
		scheduledJob.setStatus(ScheduledJobStatus.SCHEDULED);
		
		setupScheduledJob(scheduledJob);
		
		Date now = Date.from(Instant.now());
		
		scheduledJob.setCreatedDate(now);
		scheduledJob.setCreatedBy(userInfo);
		scheduledJob.setLastModifiedDate(now);
		scheduledJob.setLastModifiedBy(userInfo);
		scheduledJob.setSystemCreatedDate(now);
		scheduledJob.setSystemModifiedDate(now);
		
		mongoDocumentService.create(scheduledJob.toDocument());
		
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
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		scheduledJob.setLastModifiedDate(now);
		scheduledJob.setLastModifiedBy(userInfo);
		scheduledJob.setSystemModifiedDate(now);
		
		mongoDocumentService.replace(scheduledJob.toDocument());
		
		submitScheduledJobRequest(scheduledJob);
	}
	
	/**
	 * 
	 * @param id
	 */
	
	public ScheduledJob terminateScheduledJob(String id) {
		ScheduledJob scheduledJob = findById(id);
		scheduledJob.setStatus(ScheduledJobStatus.TERMINATED);
		mongoDocumentService.replace(scheduledJob.toDocument());
		return scheduledJob;
	}
	
	/**
	 * 
	 * @param id
	 */
	
	public ScheduledJob startScheduledJob(String id) {
		ScheduledJob scheduledJob = findById(id);
		scheduledJob.setStatus(ScheduledJobStatus.SCHEDULED);
		mongoDocumentService.replace(scheduledJob.toDocument());
		return scheduledJob;
	}
	
	/**
	 * 
	 * @param id
	 */
	
	public ScheduledJob stopScheduledJob(String id) {
		ScheduledJob scheduledJob = findById(id);
		scheduledJob.setStatus(ScheduledJobStatus.STOPPED);
		mongoDocumentService.replace(scheduledJob.toDocument());
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
		ScheduledJob resource = findById(id);
		mongoDocumentService.delete(resource);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */

	public ScheduledJob findById(String id) {
		com.nowellpoint.api.model.document.ScheduledJob document = mongoDocumentService.find(com.nowellpoint.api.model.document.ScheduledJob.class, new ObjectId( id ) );
		ScheduledJob resource = new ScheduledJob( document );
		return resource;
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
	
	public void terminateAllJobs(@Observes @Deactivate AccountProfile accountProfile) {
		ScheduledJobList list = findByOwner(accountProfile.getId());
		list.getItems().stream().forEach(scheduledJob -> {
			scheduledJob.setStatus(ScheduledJobStatus.TERMINATED);
			mongoDocumentService.replace(scheduledJob.toDocument());
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
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC"));
		Date now = Date.from(Instant.now());
		
		String collectionName = MongoDatastore.getCollectionName( com.nowellpoint.api.model.document.AccountProfile.class );
		ObjectId id = new ObjectId( System.getProperty( Properties.DEFAULT_SUBJECT ) );

		DBRef reference = new DBRef( collectionName, id );
		
		UserRef userRef = new UserRef();
		userRef.setIdentity(reference);

		ScheduledJobRequest scheduledJobRequest = new ScheduledJobRequest();
		scheduledJobRequest.setScheduledJobId(new ObjectId(scheduledJob.getId()));
		scheduledJobRequest.setConnectorId(scheduledJob.getConnectorId());
		scheduledJobRequest.setConnectorType(scheduledJob.getConnectorType());
		scheduledJobRequest.setOwner(new UserRef(new DBRef( collectionName, new ObjectId(scheduledJob.getOwner().getId() ) ) ) );
		scheduledJobRequest.setCreatedDate(now);
		scheduledJobRequest.setCreatedBy(userRef);
		scheduledJobRequest.setDescription(scheduledJob.getDescription());
		scheduledJobRequest.setEnvironmentKey(scheduledJob.getEnvironmentKey());
		scheduledJobRequest.setEnvironmentName(scheduledJob.getEnvironmentName());
		scheduledJobRequest.setIsSandbox(scheduledJob.getIsSandbox());
		scheduledJobRequest.setJobTypeCode(scheduledJob.getJobTypeCode());
		scheduledJobRequest.setJobTypeId(scheduledJob.getJobTypeCode());
		scheduledJobRequest.setJobTypeName(scheduledJob.getJobTypeName());
		scheduledJobRequest.setLastModifiedDate(now);
		scheduledJobRequest.setLastModifiedBy(userRef);
		scheduledJobRequest.setNotificationEmail(scheduledJob.getNotificationEmail());
		scheduledJobRequest.setScheduleDate(scheduledJob.getScheduleDate());
		scheduledJobRequest.setStatus(scheduledJob.getStatus());
		scheduledJobRequest.setSystemCreatedDate(now);
		scheduledJobRequest.setSystemModifiedDate(now);
		scheduledJobRequest.setYear(dateTime.getYear());
		scheduledJobRequest.setMonth(dateTime.getMonth().getValue());
		scheduledJobRequest.setDay(dateTime.getDayOfMonth());
		scheduledJobRequest.setHour(dateTime.getHour());
		scheduledJobRequest.setMinute(dateTime.getMinute());
		scheduledJobRequest.setSecond(dateTime.getSecond());
		
		MongoDatastore.getCollection( ScheduledJobRequest.class ).replaceOne( and ( 
				eq ( "scheduledJobId", scheduledJob.getId().toString() ), 
				or ( eq ( "status", "Scheduled" ), eq ( "status", "Stopped" ))), 
				scheduledJobRequest, 
				new UpdateOptions().upsert(true));
	}
}