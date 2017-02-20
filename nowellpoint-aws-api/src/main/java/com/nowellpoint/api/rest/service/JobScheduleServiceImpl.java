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
import com.nowellpoint.annotation.Deactivate;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.Backup;
import com.nowellpoint.api.rest.domain.ConnectorInfo;
import com.nowellpoint.api.rest.domain.Instance;
import com.nowellpoint.api.rest.domain.InstanceInfo;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobStatus;
import com.nowellpoint.api.rest.domain.JobTypeInfo;
import com.nowellpoint.api.rest.domain.RunHistory;
import com.nowellpoint.api.rest.domain.SalesforceConnector;
import com.nowellpoint.api.rest.domain.JobSchedule;
import com.nowellpoint.api.rest.domain.JobScheduleList;
import com.nowellpoint.api.rest.domain.JobType;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.api.service.JobScheduleService;
import com.nowellpoint.api.service.JobTypeService;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Assert;

public class JobScheduleServiceImpl extends AbstractJobScheduleService implements JobScheduleService {
	
	private static final String BUCKET_NAME = "nowellpoint-metadata-backups";
	
	@Inject
	private JobTypeService jobTypeService;
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	public JobScheduleServiceImpl() {
		
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public JobScheduleList findByOwner(String ownerId) {
		return super.findAllByOwner(ownerId);
	}
	
	/**
	 * 
	 * 
	 * 
	 */
	
	@Override
	public JobScheduleList findScheduled() {
		return super.findByStatus("Scheduled");
	}
	
	@Override
	public JobSchedule createJobSchedule(
			String jobTypeId, 
			String connectorId, 
			String instanceKey, 
			Date scheduleDate, 
			String seconds,
			String minutes,
			String hours,
			String dayOfMonth,
			String month,
			String dayOfWeek,
			String year) {
		
		JobType jobType = jobTypeService.findById(jobTypeId);
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		Date now = Date.from(Instant.now());
		
		JobSchedule jobSchedule = new JobSchedule();
		jobSchedule.setScheduleDate(Assert.isNull(scheduleDate) ? new Date() : scheduleDate);
		jobSchedule.setStatus(JobStatus.NOT_SCHEDULED);
		jobSchedule.setOwner(userInfo);
		jobSchedule.setCreatedOn(now);
		jobSchedule.setCreatedBy(userInfo);
		jobSchedule.setLastUpdatedOn(now);
		jobSchedule.setLastUpdatedBy(userInfo);
		jobSchedule.setSeconds(Assert.isNull(seconds) ? "*" : seconds);
		jobSchedule.setMinutes(Assert.isNull(minutes) ? "*" : minutes);
		jobSchedule.setHours(Assert.isNull(hours) ? "*" : hours);
		jobSchedule.setDayOfMonth(dayOfMonth);
		jobSchedule.setMonth(Assert.isNull(month) ? "*" : month);
		jobSchedule.setDayOfWeek(dayOfWeek);
		jobSchedule.setYear(Assert.isNull(year) ? "*" : year);
		
		jobSchedule.setJobType(new JobTypeInfo(jobType));
		
		/**
		 * add type specific elements
		 */
		
		if ("SALESFORCE".equals(jobSchedule.getJobType().getConnectorType().getCode())) {
			
			SalesforceConnector salesforceConnector = null;
			try {
				salesforceConnector = salesforceConnectorService.findById( connectorId );
			} catch (DocumentNotFoundException e) {
				throw new ValidationException(String.format("Invalid Connector Id: %s for SalesforceConnector", connectorId));
			}

			Optional<Instance> instance = null;
			
			if (Assert.isNullOrEmpty(instanceKey)) {
				instance = salesforceConnector.getInstances()
						.stream()
						.filter(e -> ! e.getIsSandbox())
						.findFirst();				
			} else {
				instance = salesforceConnector.getInstances()
						.stream()
						.filter(i -> instanceKey.equals(i.getKey()))
						.findFirst();
				
				if (! instance.isPresent()) {
					throw new ValidationException(String.format("Invalid instance key: %s", instanceKey));
				}
			}

			if (jobSchedule.getNotificationEmail() == null) {
				jobSchedule.setNotificationEmail(instance.get().getEmail());
			}
			
			InstanceInfo instanceInfo = new InstanceInfo();
			instanceInfo.setApiVersion(instance.get().getApiVersion());
			instanceInfo.setIsSandbox(instance.get().getIsSandbox());
			instanceInfo.setKey(instance.get().getKey());
			instanceInfo.setName(instance.get().getName());
			instanceInfo.setServiceEndpoint(instance.get().getServiceEndpoint());
			
			ConnectorInfo connectorInfo = new ConnectorInfo();
			connectorInfo.setId(salesforceConnector.getId());
			connectorInfo.setName(salesforceConnector.getName());
			connectorInfo.setOrganizationName(salesforceConnector.getOrganization().getName());
			connectorInfo.setServerName(salesforceConnector.getOrganization().getInstanceName());
			connectorInfo.setInstance(instanceInfo);
			
			jobSchedule.setConnector(connectorInfo);
			
		}
		
		create(jobSchedule);
		
		return jobSchedule;
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param jobSchedule
	 * 
	 * 
	 */
	
	@Override
	public void updateScheduledJob(String id, JobSchedule jobSchedule) {
		
		/**
		 * retrieve the original record
		 */
		
		JobSchedule original = findById(id);
		
		/**
		 * validation steps
		 */
		
		if (JobStatus.TERMINATED.equals(original.getStatus())) {
			throw new ValidationException( "Scheduled Job has been terminated and cannot be altered" );
		}
		
		if (jobSchedule.getScheduleDate().before(Date.from(Instant.now()))) {
			throw new ValidationException( "Schedule Date cannot be before current date" );
		}
		
		if (! (JobStatus.SCHEDULED.equals(jobSchedule.getStatus()) || JobStatus.STOPPED.equals(jobSchedule.getStatus())) || JobStatus.TERMINATED.equals(jobSchedule.getStatus())) {
			throw new ValidationException( String.format( "Invalid status: %s", jobSchedule.getStatus() ) );
		}
		
		/**
		 * add read-only fields
		 */
		
		jobSchedule.setId(id);
		jobSchedule.setStatus(original.getStatus());
		jobSchedule.setJobType(original.getJobType());
		jobSchedule.setConnector(original.getConnector());
		jobSchedule.setCreatedOn(original.getCreatedOn());
		jobSchedule.setLastRunDate(original.getLastRunDate());
		jobSchedule.setLastRunStatus(original.getLastRunStatus());
		jobSchedule.setLastRunFailureMessage(original.getLastRunFailureMessage());
		jobSchedule.setCreatedBy(original.getCreatedBy());
		
		/**
		 * ensure a complete record for fields that can be updated
		 */
		
		if (isNull(jobSchedule.getDescription())) {
			jobSchedule.setDescription(original.getDescription());
		} else if (isEmpty(jobSchedule.getDescription())) {
			jobSchedule.setDescription(null);
		}

		if (isNull(jobSchedule.getOwner())) {
			jobSchedule.setOwner(original.getOwner());
		}
		
		if (isNull(jobSchedule.getScheduleDate())) {
			jobSchedule.setScheduleDate(original.getScheduleDate());
		}
		
		if (isNull(jobSchedule.getStatus())) {
			jobSchedule.setStatus(original.getStatus());
		}
		
		if (isNullOrEmpty(jobSchedule.getNotificationEmail())) {
			jobSchedule.setNotificationEmail(original.getNotificationEmail());
		}
		
		if (isNull(jobSchedule.getRunHistories())) {
			jobSchedule.setRunHistories(original.getRunHistories());
		}
		
		if (isNullOrEmpty(jobSchedule.getSeconds())) {
			jobSchedule.setSeconds("*");
		}
		
		if (isNullOrEmpty(jobSchedule.getMinutes())) {
			jobSchedule.setMinutes("*");
		}
		
		if (isNullOrEmpty(jobSchedule.getHours())) {
			jobSchedule.setHours("*");
		}
		
		if (isNullOrEmpty(jobSchedule.getMonth())) {
			jobSchedule.setMonth("*");
		}
		
		if (isNullOrEmpty(jobSchedule.getYear())) {
			jobSchedule.setYear("*");
		}

		/**
		 * add type specific elements
		 */
		
//		if ("SALESFORCE".equals(jobSchedule.getJobType().getConnectorType().getCode())) {
//			
//			SalesforceConnector salesforceConnector = null;
//			try {
//				salesforceConnector = salesforceConnectorService.findById( jobSchedule.getConnector().getId() );
//			} catch (DocumentNotFoundException e) {
//				throw new ValidationException(String.format("Invalid Connector Id: %s for SalesforceConnector", jobSchedule.getConnector().getId()));
//			}
//
//			Optional<Instance> instance = null;
//			if (jobSchedule.getConnector().getInstance().getKey() != null && ! jobSchedule.getConnector().getInstance().getKey().trim().isEmpty()) {
//				instance = salesforceConnector.getInstances()
//						.stream()
//						.filter(e -> jobSchedule.getConnector().getInstance().getKey().equals(e.getKey()))
//						.findFirst();
//				
//				if (! instance.isPresent()) {
//					throw new ValidationException(String.format("Invalid environment key: %s", jobSchedule.getConnector().getInstance().getKey()));
//				}
//				
//			} else {
//				instance = salesforceConnector.getInstances().stream().filter(e -> ! e.getIsSandbox()).findFirst();
//			}
//
//			if (jobSchedule.getNotificationEmail() == null) {
//				jobSchedule.setNotificationEmail(instance.get().getEmail());
//			}
//			
//			ConnectorInfo connectorInfo = new ConnectorInfo();
//			connectorInfo.setId(salesforceConnector.getId());
//			connectorInfo.setName(salesforceConnector.getName());
//			connectorInfo.setOrganizationName(salesforceConnector.getOrganization().getName());
//			
//		}
		
		/**
		 * add audit fields
		 */
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		jobSchedule.setLastUpdatedOn(now);
		jobSchedule.setLastUpdatedBy(userInfo);
		
		/**
		 * perform update
		 */
		
		update(jobSchedule);
		
		/**
		 * submit the scheduled job to the job operator
		 */
		
		submitJob(jobSchedule);
	}
	
	/**
	 * 
	 * @param id
	 */
	
	@Override
	public JobSchedule terminateScheduledJob(String id) {
		JobSchedule jobSchedule = findById(id);
		jobSchedule.setStatus(JobStatus.TERMINATED);
		update(jobSchedule);
		return jobSchedule;
	}
	
	/**
	 * 
	 * @param id
	 */
	
	@Override
	public JobSchedule startScheduledJob(String id) {
		JobSchedule jobSchedule = findById(id);
		jobSchedule.setStatus(JobStatus.SCHEDULED);
		update(jobSchedule);
		return jobSchedule;
	}
	
	/**
	 * 
	 * @param id
	 */
	
	@Override
	public JobSchedule stopScheduledJob(String id) {
		JobSchedule jobSchedule = findById(id);
		jobSchedule.setStatus(JobStatus.STOPPED);
		update(jobSchedule);
		return jobSchedule;
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
		JobSchedule resource = findById(id);
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
	public JobSchedule findById(String id) {
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
		JobSchedule jobSchedule = findById( id );
		
		Optional<RunHistory> filter = jobSchedule.getRunHistories()
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
		JobScheduleList list = findByOwner(accountProfile.getId());
		list.getItems().stream().forEach(scheduledJob -> {
			scheduledJob.setStatus(JobStatus.TERMINATED);
			update(scheduledJob);
		});
	}
	
	private void submitJob(JobSchedule jobSchedule) {
		Date now = Date.from(Instant.now());
		
		Job job = new Job();
		job.setCreatedBy(jobSchedule.getCreatedBy());
		job.setCreatedOn(now);
		job.setDayOfMonth(jobSchedule.getDayOfMonth());
		job.setDayOfWeek(jobSchedule.getDayOfWeek());
		job.setHours(jobSchedule.getHours());
		job.setJobName(jobSchedule.getJobType().getCode());
		job.setLastUpdatedBy(jobSchedule.getLastUpdatedBy());
		job.setLastUpdatedOn(now);
		job.setMinutes(jobSchedule.getMinutes());
		job.setMonth(jobSchedule.getMonth());
		job.setSeconds(jobSchedule.getSeconds());
		job.setStatus(JobStatus.SCHEDULED);
		job.setYear(jobSchedule.getYear());
		
		submit(job);
	}
}