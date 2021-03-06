package com.nowellpoint.client.model;

import java.util.Date;

public class JobOutput {
	
	private String fireInstanceId;
	
	private String type;
	
	private String filename;
	
	private String fileSizeInUnits;
	
	private Date addedOn;
	
	public JobOutput() {
		
	}

	public String getFireInstanceId() {
		return fireInstanceId;
	}

	public void setFireInstanceId(String fireInstanceId) {
		this.fireInstanceId = fireInstanceId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFileSizeInUnits() {
		return fileSizeInUnits;
	}

	public void setFileSizeInUnits(String fileSizeInUnits) {
		this.fileSizeInUnits = fileSizeInUnits;
	}

	public Date getAddedOn() {
		return addedOn;
	}

	public void setAddedOn(Date addedOn) {
		this.addedOn = addedOn;
	}
}