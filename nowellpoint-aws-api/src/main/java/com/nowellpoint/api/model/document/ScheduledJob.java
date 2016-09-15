package com.nowellpoint.api.model.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.api.model.codec.ScheduledJobCodec;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.MongoDocument;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="scheduled.jobs", codec=ScheduledJobCodec.class)
public class ScheduledJob extends MongoDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 4880299116047933778L;
	
	private User owner;
	
	private Reference connector;
	
	private Reference scheduledJob;
	
	private String description;
	
	private Schedule schedule;
	
	public ScheduledJob() {
		
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
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
}