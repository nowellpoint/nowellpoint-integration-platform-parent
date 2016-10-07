package com.nowellpoint.api.service;

import static com.nowellpoint.util.Assert.isEmpty;
import static com.nowellpoint.util.Assert.isNull;
import static com.nowellpoint.util.Assert.isNullOrEmpty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.Environment;
import com.nowellpoint.api.model.dto.SalesforceConnector;
import com.nowellpoint.api.model.dto.ScheduledJob;
import com.nowellpoint.api.model.dto.ScheduledJobType;
import com.nowellpoint.api.model.dto.UserInfo;
import com.nowellpoint.api.model.mapper.ScheduledJobModelMapper;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;

public class ScheduledJobService extends ScheduledJobModelMapper {
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private ScheduledJobTypeService scheduledJobTypeService;
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	@Inject
	private PaymentGatewayService paymentGatewayService;
	
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
	
	public Set<ScheduledJob> findAllByOwner() {
		return super.findAllByOwner();
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
		
		UserInfo createdBy = new UserInfo(getSubject());
		
		scheduledJob.setCreatedBy(createdBy);
		scheduledJob.setLastModifiedBy(createdBy);
		scheduledJob.setStatus("Scheduled");
		
		setupScheduledJob(scheduledJob);
		
		if (! scheduledJob.getIsSandbox()) {
			AccountProfile accountProfile = accountProfileService.findAccountProfile(getSubject());
			paymentGatewayService.addMonthlyRecurringPlan(accountProfile.getPrimaryCreditCard().getToken(), new BigDecimal("7.00"));
		}
		
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
		
		if ("Terminated".equals(original.getStatus())) {
			throw new ServiceException( Status.FORBIDDEN, "Scheduled Job has been terminated and cannot be altered" );
		}
		
		scheduledJob.setId(id);
		scheduledJob.setJobTypeId(original.getJobTypeId());
		scheduledJob.setJobTypeCode(original.getJobTypeCode());
		scheduledJob.setJobTypeName(original.getJobTypeName());
		scheduledJob.setCreatedById(original.getCreatedById());
		scheduledJob.setCreatedDate(original.getCreatedDate());
		scheduledJob.setSystemCreationDate(original.getSystemCreationDate());
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
				
		scheduledJob.setLastModifiedBy(new UserInfo(getSubject()));
		
		setupScheduledJob(scheduledJob);
		
		super.updateScheduledJob(scheduledJob);
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
	 * @param scheduledJob
	 * 
	 * 
	 */
	
	private void setupScheduledJob(ScheduledJob scheduledJob) {
		
		if (scheduledJob.getScheduleDate().before(Date.from(Instant.now()))) {
			throw new ServiceException(Status.BAD_REQUEST, "Schedule Date cannot be before current date");
		}
		
		if (! ("Scheduled".equals(scheduledJob.getStatus()) || "Stopped".equals(scheduledJob.getStatus())) || "Terminated".equals(scheduledJob.getStatus())) {
			throw new ServiceException( Status.BAD_REQUEST, String.format( "Invalid status: %s", scheduledJob.getStatus() ) );
		}
		
		ScheduledJobType scheduledJobType = scheduledJobTypeService.findScheduedJobTypeById( scheduledJob.getJobTypeId() );

		if ("SALESFORCE".equals(scheduledJobType.getConnectorType().getCode())) {
			
			SalesforceConnector salesforceConnector = null;
			try {
				salesforceConnector = salesforceConnectorService.findSalesforceConnector( scheduledJob.getConnectorId() );
			} catch (DocumentNotFoundException e) {
				throw new ServiceException(String.format("Invalid Connector Id: %s for SalesforceConnector", scheduledJob.getConnectorId()));
			}

			Optional<Environment> environment = null;
			if (scheduledJob.getEnvironmentKey() != null && ! scheduledJob.getEnvironmentKey().trim().isEmpty()) {
				environment = salesforceConnector.getEnvironments()
						.stream()
						.filter(e -> scheduledJob.getEnvironmentKey().equals(e.getKey()))
						.findFirst();
				
				if (! environment.isPresent()) {
					throw new ServiceException(String.format("Invalid environment key: %s", scheduledJob.getEnvironmentKey()));
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