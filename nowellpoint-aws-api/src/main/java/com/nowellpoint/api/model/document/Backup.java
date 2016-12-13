package com.nowellpoint.api.model.document;

public class Backup {
	
	private String type;
	
	private String filename;
	
	private Long filesize;
	
	public Backup() {
		
	}
	
	public Backup(String type, String filename, Long filesize) {
		this.type = type;
		this.filename = filename;
		this.filesize = filesize;
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
}