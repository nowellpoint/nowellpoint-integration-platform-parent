package com.nowellpoint.api.rest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JobOutput {
	
	private String type;
	
	private String filename;
	
	private Long filesize;
	
	@JsonIgnore
	private String bucket;
	
	@JsonIgnore
	private String key;
	
	public JobOutput() {
		
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
}