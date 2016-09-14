package com.nowellpoint.api.service;

import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.Schedule;
import com.nowellpoint.api.model.dto.ScheduledJob;
import com.nowellpoint.api.model.mapper.ScheduledJobModelMapper;

public class ScheduledJobService extends ScheduledJobModelMapper {
	
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
		
		super.createScheduledJob(scheduledJob);
	}
	
	public void updateScheduledJob(Id id, ScheduledJob scheduledJob) {
		ScheduledJob original = findScheduledJobById(id);
		
		scheduledJob.setId(id);
		scheduledJob.setCreatedById(original.getCreatedById());
		scheduledJob.setCreatedDate(original.getCreatedDate());
		scheduledJob.setSystemCreationDate(original.getSystemCreationDate());
		
		if (scheduledJob.getConnector() == null) {
			scheduledJob.setConnector(original.getConnector());
		}
		
		if (scheduledJob.getScheduledJob() == null) {
			scheduledJob.setScheduledJob(original.getScheduledJob());
		}
		
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
	
	public Schedule updateSchedule(Id id, String key, MultivaluedMap<String, String> parameters) {
		Schedule schedule = new Schedule();
		
		if (parameters.containsKey("hour")) {
			schedule.setHour(Integer.valueOf(parameters.getFirst("hour")));
		}
		
		if (parameters.containsKey("minute")) {
			schedule.setMinute(Integer.valueOf(parameters.getFirst("minute")));
		}
		
		if (parameters.containsKey("second")) {
			schedule.setSecond(Integer.valueOf(parameters.getFirst("second")));
		}

		if (parameters.containsKey("status")) {
			schedule.setStatus(parameters.getFirst("status"));
		}
		
		updateSchedule(id, schedule);
		
		return schedule;
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @param schedule
	 * 
	 * 
	 */
	
	public void updateSchedule(Id id, Schedule schedule) {
		ScheduledJob scheduledJob = findScheduledJobById(id);
		super.updateScheduledJob(scheduledJob);
	}
}