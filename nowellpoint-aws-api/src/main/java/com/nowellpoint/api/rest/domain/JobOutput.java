package com.nowellpoint.api.rest.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JobOutput {
	
	private String fireInstanceId;
	
	private String type;
	
	private String filename;
	
	private String fileSizeInUnits;
	
	@JsonIgnore
	private Long filesize;
	
	@JsonIgnore
	private String bucket;
	
	@JsonIgnore
	private String key;
	
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

	public Long getFilesize() {
		return filesize;
	}

	public void setFilesize(Long filesize) {
		this.filesize = filesize;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getAddedOn() {
		return addedOn;
	}

	public void setAddedOn(Date addedOn) {
		this.addedOn = addedOn;
	}
}