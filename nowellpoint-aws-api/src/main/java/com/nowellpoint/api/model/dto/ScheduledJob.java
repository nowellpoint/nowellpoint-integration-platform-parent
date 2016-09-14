package com.nowellpoint.api.model.dto;

import com.nowellpoint.client.model.Reference;

public class ScheduledJob extends AbstractResource {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1699588438360478864L;
	
	private AccountProfile owner;
	
	private Reference connector;
	
	private Reference scheduledJob;
	
	private String description;
	
	private Schedule schedule;
	
	public ScheduledJob() {
		
	}

	public AccountProfile getOwner() {
		return owner;
	}

	public void setOwner(AccountProfile owner) {
		this.owner = owner;
	}

	public Reference getConnector() {
		return connector;
	}

	public void setConnector(Reference connector) {
		this.connector = connector;
	}

	public Reference getScheduledJob() {
		return scheduledJob;
	}

	public void setScheduledJob(Reference scheduledJob) {
		this.scheduledJob = scheduledJob;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}