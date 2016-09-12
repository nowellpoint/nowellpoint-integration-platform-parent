package com.nowellpoint.api.model.dto;

import java.util.HashSet;
import java.util.Set;

public class ScheduledJob extends AbstractResource {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1699588438360478864L;
	
	private AccountProfile owner;
	
	private String connectorId;
	
	private String connectorType;
	
	private String name;
	
	private String description;

	private String jobType;
	
	private String jobName;
	
	private Set<Schedule> schedules;
	
	public ScheduledJob() {
		
	}

	public AccountProfile getOwner() {
		return owner;
	}

	public void setOwner(AccountProfile owner) {
		this.owner = owner;
	}

	public String getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(String connectorId) {
		this.connectorId = connectorId;
	}

	public String getConnectorType() {
		return connectorType;
	}

	public void setConnectorType(String connectorType) {
		this.connectorType = connectorType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Set<Schedule> getSchedules() {
		return schedules;
	}

	public void setSchedules(Set<Schedule> schedules) {
		this.schedules = schedules;
	}
	
	public void addSchedule(Schedule schedule) {
		if (schedules == null) {
			schedules = new HashSet<Schedule>();
		}
		schedules.add(schedule);
	}
}