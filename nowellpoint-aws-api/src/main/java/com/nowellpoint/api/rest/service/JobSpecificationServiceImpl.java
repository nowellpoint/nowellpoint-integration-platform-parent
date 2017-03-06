package com.nowellpoint.api.rest.service;

import static com.nowellpoint.util.Assert.isEmpty;
import static com.nowellpoint.util.Assert.isNotNullOrEmpty;
import static com.nowellpoint.util.Assert.isNull;
import static com.nowellpoint.util.Assert.isNullOrEmpty;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.ValidationException;
import javax.ws.rs.BadRequestException;

import org.jboss.logging.Logger;

import com.nowellpoint.api.rest.domain.ConnectorInfo;
import com.nowellpoint.api.rest.domain.Instance;
import com.nowellpoint.api.rest.domain.InstanceInfo;
import com.nowellpoint.api.rest.domain.JobSpecification;
import com.nowellpoint.api.rest.domain.JobSpecificationList;
import com.nowellpoint.api.rest.domain.JobStatus;
import com.nowellpoint.api.rest.domain.JobType;
import com.nowellpoint.api.rest.domain.JobTypeInfo;
import com.nowellpoint.api.rest.domain.SalesforceConnector;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.JobSpecificationService;
import com.nowellpoint.api.service.JobTypeService;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Assert;

public class JobSpecificationServiceImpl extends AbstractJobSpecificationService implements JobSpecificationService {
	
	private static final Logger LOGGER = Logger.getLogger(JobSpecificationServiceImpl.class);
	
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
	
	public JobSpecificationServiceImpl() {
		
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	@Override
	public JobSpecificationList findByOwner(String ownerId) {
		return super.findAllByOwner(ownerId);
	}
	
	/**
	 * 
	 * 
	 * 
	 */
	
	@Override
	public void createJobSpecification(JobSpecification jobSpecification) {
		create(jobSpecification);
	}
	
	@Override
	public void udpateJobSpecification(String id, JobSpecification jobSpecification) {
		JobSpecification original = findById(id);
		
	}
	
	@Override
	public JobSpecification createJobSpecification(
			String jobTypeId, 
			String connectorId, 
			String instanceKey, 
			String start, 
			String end,
			String timeZone,
			String seconds,
			String minutes,
			String hours,
			String dayOfMonth,
			String month,
			String dayOfWeek,
			String year,
			String notificationEmail,
			String description) {
		
		LocalDate startDate = null;
		LocalDate endDate = null;
		
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		if (isNotNullOrEmpty(start)) {
			try {
				startDate = LocalDate.parse(start, format);
			} catch (Exception e) {
				LOGGER.warn(e.getMessage());
				throw new BadRequestException(e.getMessage());
			}
		}
		
		if (isNotNullOrEmpty(end)) {
			try {
				endDate = LocalDate.parse(end, format);
			} catch (Exception e) {
				LOGGER.warn(e.getMessage());
				throw new BadRequestException(e.getMessage());
			}
		}
		
		timeZone = Assert.isNullOrEmpty(timeZone) ? ZoneId.systemDefault().getId() : timeZone;
		
		if (Assert.isNotNull(startDate) && startDate.isBefore(LocalDate.now(ZoneId.of(timeZone)))) {
			throw new ValidationException( "Schedule Date cannot be before current date" );
		}
		
		JobType jobType = jobTypeService.findById(jobTypeId);
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		Date now = Date.from(Instant.now());
		
		JobSpecification jobSpecification = new JobSpecification();
		//jobSpecification.setStart(Assert.isNotNull(startDate) ? Date.from(startDate.atStartOfDay().atZone(ZoneId.of(timeZone)).toInstant()) : new Date());
		//jobSpecification.setEnd(Assert.isNotNull(endDate) ? Date.from(endDate.atStartOfDay().atZone(ZoneId.of(timeZone)).toInstant()) : null);
		//jobSpecification.setTimeZone(timeZone);
		//jobSpecification.setStatus(JobStatus.NOT_SCHEDULED);
		jobSpecification.setOwner(userInfo);
		jobSpecification.setCreatedOn(now);
		jobSpecification.setCreatedBy(userInfo);
		jobSpecification.setLastUpdatedOn(now);
		jobSpecification.setLastUpdatedBy(userInfo);
		jobSpecification.setDescription(description);
		jobSpecification.setNotificationEmail(notificationEmail);
		//jobSpecification.setSeconds(Assert.isNullOrEmpty(seconds) ? "*" : seconds);
		//jobSpecification.setMinutes(Assert.isNullOrEmpty(minutes) ? "*" : minutes);
		//jobSpecification.setHours(Assert.isNullOrEmpty(hours) ? "*" : hours);
		//jobSpecification.setDayOfMonth(Assert.isNullOrEmpty(dayOfMonth) ? null : dayOfMonth);
		//jobSpecification.setMonth(Assert.isNullOrEmpty(month) ? "*" : month);
		//jobSpecification.setDayOfWeek(Assert.isNullOrEmpty(dayOfWeek) ? null : dayOfWeek);
		//jobSpecification.setYear(Assert.isNullOrEmpty(year) ? "*" : year);
		
		/**
		 * add type specific elements
		 */
		
		if ("SALESFORCE".equals(jobSpecification.getJobType().getConnectorType().getCode())) {
			
			SalesforceConnector salesforceConnector = null;
			try {
				salesforceConnector = salesforceConnectorService.findById( connectorId );
			} catch (DocumentNotFoundException e) {
				throw new ValidationException(String.format("Invalid Connector Id: %s for SalesforceConnector", connectorId));
			}

			if (Assert.isNullOrEmpty(jobSpecification.getNotificationEmail())) {
				jobSpecification.setNotificationEmail(salesforceConnector.getIdentity().getEmail());
			}
			
			ConnectorInfo connectorInfo = new ConnectorInfo();
			connectorInfo.setId(salesforceConnector.getId());
			connectorInfo.setName(salesforceConnector.getName());
			connectorInfo.setOrganizationName(salesforceConnector.getOrganization().getName());
			connectorInfo.setServerName(salesforceConnector.getOrganization().getInstanceName());
			
			jobSpecification.setConnector(connectorInfo);
			
		}
		
		create(jobSpecification);
		
		return jobSpecification;
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
	public JobSpecification updateJobSpecification(
			String id, 
			String start, 
			String end,
			String timeZone,
			String seconds,
			String minutes,
			String hours,
			String dayOfMonth,
			String month,
			String dayOfWeek,
			String year,
			String notificationEmail,
			String description) {
		
		/**
		 * retrieve the original record
		 */
		
		JobSpecification original = findById(id);
		
		LocalDate startDate = null;
		LocalDate endDate = null;
		
		if (isNotNullOrEmpty(start)) {
			try {
				DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				startDate = LocalDate.parse(start, format);
			} catch (Exception e) {
				LOGGER.warn(e.getMessage());
				throw new BadRequestException(e.getMessage());
			}
		}
		
		if (isNotNullOrEmpty(end)) {
			try {
				DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				endDate = LocalDate.parse(end, format);
			} catch (Exception e) {
				LOGGER.warn(e.getMessage());
				throw new BadRequestException(e.getMessage());
			}
		}
		
		/**
		 * validation steps
		 */
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		Date now = Date.from(Instant.now());
		
		JobSpecification jobSchedule = new JobSpecification();
		jobSchedule.setId(id);
		jobSchedule.setLastUpdatedOn(now);
		jobSchedule.setLastUpdatedBy(userInfo);
		jobSchedule.setDescription(description);
		jobSchedule.setNotificationEmail(notificationEmail);
		
		/**
		 * add read-only fields
		 */
		
		jobSchedule.setJobType(original.getJobType());
		jobSchedule.setConnector(original.getConnector());
		jobSchedule.setCreatedOn(original.getCreatedOn());
		jobSchedule.setCreatedBy(original.getCreatedBy());
		jobSchedule.setOwner(original.getOwner());
		
		/**
		 * validate the JobSchedule record to ensure a complete record for fields that can be updated
		 */
		
		if (isNull(jobSchedule.getDescription())) {
			jobSchedule.setDescription(original.getDescription());
		} else if (isEmpty(jobSchedule.getDescription())) {
			jobSchedule.setDescription(null);
		}

		if (isNull(jobSchedule.getOwner())) {
			jobSchedule.setOwner(original.getOwner());
		}
		
		if (isNullOrEmpty(jobSchedule.getNotificationEmail())) {
			jobSchedule.setNotificationEmail(original.getNotificationEmail());
		}
		
		/**
		 * perform update
		 */
		
		update(jobSchedule);
		
		/**
		 * return the result
		 */
		
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
	public void deleteJobSpecification(String id) {
		JobSpecification resource = findById(id);
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
	public JobSpecification findById(String id) {
		return super.findById(id);
	}
}