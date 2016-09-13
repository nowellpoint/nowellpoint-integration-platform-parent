package com.nowellpoint.api.service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;

import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.SalesforceConnector;
import com.nowellpoint.api.model.dto.Schedule;
import com.nowellpoint.api.model.dto.ScheduledJob;
import com.nowellpoint.api.model.mapper.ScheduledJobModelMapper;

public class ScheduledJobService extends ScheduledJobModelMapper {
	
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
		
		if ("SALESFORCE".equals(scheduledJob.getConnectorType())) {
			SalesforceConnector salesforceConnector = salesforceConnectorService.findSalesforceConnector( new Id( scheduledJob.getConnectorId() ) );
			
			scheduledJob.setJobType("METADATA_BACKUP");
			scheduledJob.setJobName("Metadata Backup");
			
			salesforceConnector.getEnvironments().stream().filter(e -> ! e.getIsSandbox()).forEach(e -> {
				Schedule schedule = new Schedule();
				schedule.setKey(UUID.randomUUID().toString().replaceAll("-", ""));
				schedule.setHour(00);
				schedule.setMinute(00);
				schedule.setSecond(00);
				schedule.setStatus("Not Started");
				schedule.setAddedOn(Date.from(Instant.now()));
				schedule.setEnvironmentKey(e.getKey());
				schedule.setEnvironmentName(e.getEnvironmentName());
				schedule.setUpdatedOn(Date.from(Instant.now()));
				scheduledJob.addSchedule(schedule);
			});
		}
		
		super.createScheduledJob(scheduledJob);
	}
	
	public void updateScheduledJob(Id id, ScheduledJob scheduledJob) {
		ScheduledJob original = findScheduledJobById(id);
		
		scheduledJob.setId(id);
		scheduledJob.setCreatedById(original.getCreatedById());
		scheduledJob.setCreatedDate(original.getCreatedDate());
		scheduledJob.setSystemCreationDate(original.getSystemCreationDate());
		scheduledJob.setConnectorId(original.getConnectorId());
		scheduledJob.setConnectorType(original.getConnectorType());
		scheduledJob.setJobType(original.getJobType());
		scheduledJob.setJobName(original.getJobName());
		scheduledJob.setSchedules(original.getSchedules());
		
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
	
	public Schedule getSchedule(Id id, String key) {
		ScheduledJob scheduledJob = findScheduledJobById(id);
		
		Optional<Schedule> schedule = scheduledJob.getSchedules()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst();
		
		if (schedule.isPresent()) {
			return schedule.get();
		} else {
			return null;
		}
	}
	
	public Schedule updateSchedule(Id id, String key, MultivaluedMap<String, String> parameters) {
		Schedule schedule = new Schedule();
		schedule.setKey(key);
		
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
		
		Schedule original = getSchedule(id, schedule.getKey());
		
		if (original == null) {
			return;
		}
		
		scheduledJob.getSchedules().removeIf(s -> schedule.getKey().equals(s.getKey()));
		
		schedule.setAddedOn(original.getAddedOn());
		schedule.setUpdatedOn(Date.from(Instant.now()));
		schedule.setEnvironmentKey(original.getEnvironmentKey());
		schedule.setEnvironmentName(original.getEnvironmentName());		
		
		if (schedule.getHour() == null) {
			schedule.setHour(original.getHour());
		}
		
		if (schedule.getMinute() == null) {
			schedule.setMinute(original.getMinute());
		}
		
		if (schedule.getSecond() == null) {
			schedule.setSecond(original.getSecond());
		}
		
		if (schedule.getStatus() == null) {
			schedule.setStatus(original.getStatus());
		}
		
		scheduledJob.addSchedule(schedule);
	}
}