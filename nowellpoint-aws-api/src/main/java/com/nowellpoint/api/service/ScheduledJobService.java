package com.nowellpoint.api.service;

import java.util.Set;

import javax.inject.Inject;

import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.ScheduledJob;
import com.nowellpoint.api.model.dto.ScheduledJobType;
import com.nowellpoint.api.model.mapper.ScheduledJobModelMapper;

public class ScheduledJobService extends ScheduledJobModelMapper {
	
	@Inject
	private ScheduledJobTypeService scheduledJobTypeService;
	
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