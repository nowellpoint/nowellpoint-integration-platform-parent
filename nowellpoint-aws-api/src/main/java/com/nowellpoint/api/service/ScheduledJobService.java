package com.nowellpoint.api.service;

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

	public Set<ScheduledJob> findAllByOwner() {
		return super.findAllByOwner();
	}
	
	public void createScheduledJob(ScheduledJob scheduledJob) {
		if (scheduledJob.getOwner() == null) {
			AccountProfile owner = new AccountProfile(getSubject());
			scheduledJob.setOwner(owner);
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
			if (scheduledJob.getEnvironmentKey() != null) {
				environment = salesforceConnector.getEnvironments().stream().filter(e -> scheduledJob.getEnvironmentKey().equals(e.getKey())).findFirst();
				if (! environment.isPresent()) {
					throw new ServiceException(String.format("Invalid environment key: %s", scheduledJob.getEnvironmentKey()));
				}
			} else {
				environment = salesforceConnector.getEnvironments().stream().filter(e -> ! e.getIsSandbox()).findFirst();
			}
			scheduledJob.setEnvironmentName(environment.get().getEnvironmentName());
		}
		
		scheduledJob.setConnectorType(scheduledJobType.getConnectorType().getCode());
		scheduledJob.setJobTypeCode(scheduledJobType.getCode());
		scheduledJob.setJobTypeName(scheduledJobType.getName());
		
		if (scheduledJob.getStatus() == null) {
			scheduledJob.setStatus("NotStarted");
		}

		super.createScheduledJob(scheduledJob);
	}
	
	public void updateScheduledJob(Id id, ScheduledJob scheduledJob) {
		ScheduledJob original = findScheduledJobById(id);
		
		scheduledJob.setId(id);
		scheduledJob.setJobTypeId(original.getJobTypeId());
		scheduledJob.setJobTypeCode(original.getJobTypeCode());
		scheduledJob.setJobTypeName(original.getJobTypeName());
		scheduledJob.setCreatedById(original.getCreatedById());
		scheduledJob.setCreatedDate(original.getCreatedDate());
		scheduledJob.setSystemCreationDate(original.getSystemCreationDate());
		
		if (scheduledJob.getOwner() == null) {
			scheduledJob.setOwner(original.getOwner());
		}
		
		super.updateScheduledJob(scheduledJob);
	}
	
	public void deleteScheduledJob(Id id) {
		super.deleteScheduledJob(findScheduledJobById(id));
	}
	
	public ScheduledJob findScheduledJobById(Id id) {
		return super.findScheduedJobById(id);
	}
}