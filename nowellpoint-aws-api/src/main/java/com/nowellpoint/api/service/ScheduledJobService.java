package com.nowellpoint.api.service;

import static com.nowellpoint.util.Assert.isEmpty;
import static com.nowellpoint.util.Assert.isNull;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.Environment;
import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.SalesforceConnector;
import com.nowellpoint.api.model.dto.ScheduledJob;
import com.nowellpoint.api.model.dto.ScheduledJobType;
import com.nowellpoint.api.model.mapper.ScheduledJobModelMapper;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;

public class ScheduledJobService extends ScheduledJobModelMapper {
	
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
			AccountProfile owner = new AccountProfile(getSubject());
			scheduledJob.setOwner(owner);
		}
		
		setupScheduledJob(scheduledJob);
		
		scheduledJob.setStatus("Scheduled");

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
	
	public void updateScheduledJob(Id id, ScheduledJob scheduledJob) {
		ScheduledJob original = findScheduledJobById(id);
		
		scheduledJob.setId(id);
		scheduledJob.setJobTypeId(original.getJobTypeId());
		scheduledJob.setJobTypeCode(original.getJobTypeCode());
		scheduledJob.setJobTypeName(original.getJobTypeName());
		scheduledJob.setCreatedById(original.getCreatedById());
		scheduledJob.setCreatedDate(original.getCreatedDate());
		scheduledJob.setSystemCreationDate(original.getSystemCreationDate());
		
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
	
	public void deleteScheduledJob(Id id) {
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

	public ScheduledJob findScheduledJobById(Id id) {
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
		
		if (! (scheduledJob.getStatus().equals("Scheduled") || scheduledJob.getStatus().equals("Deactivated"))) {
			throw new ServiceException( Status.BAD_REQUEST, String.format( "Invalid status: %s", scheduledJob.getStatus() ) );
		}
		
		ScheduledJobType scheduledJobType = scheduledJobTypeService.findById(new Id(scheduledJob.getJobTypeId()));

		if ("SALESFORCE".equals(scheduledJobType.getConnectorType().getCode())) {
			SalesforceConnector salesforceConnector = null;
			try {
				salesforceConnector = salesforceConnectorService.findSalesforceConnector( new Id( scheduledJob.getConnectorId() ) );
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

			scheduledJob.setEnvironmentKey(environment.get().getKey());
			scheduledJob.setEnvironmentName(environment.get().getEnvironmentName());
		}

		scheduledJob.setConnectorType(scheduledJobType.getConnectorType().getCode());
		scheduledJob.setJobTypeCode(scheduledJobType.getCode());
		scheduledJob.setJobTypeName(scheduledJobType.getName());

	}
}