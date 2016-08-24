package com.nowellpoint.aws.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.api.codec.BatchJobCodec;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.mongodb.AbstractDocument;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="batch.jobs", codec=BatchJobCodec.class)
public class BatchJob extends AbstractDocument implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 4880299116047933778L;
	
	private Schedule schedule;
	
	public BatchJob() {
		
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
}