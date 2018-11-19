package com.nowellpoint.client.sforce.model;

import java.util.Set;

public class UserLicenseQueryResult {
	
	private Integer totalSize;
	
	private Boolean done;
	
	private Set<UserLicense> records;
	
	public UserLicenseQueryResult() {
		
	}

	public Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean isDone) {
		this.done = isDone;
	}

	public Set<UserLicense> getRecords() {
		return records;
	}

	public void setRecords(Set<UserLicense> records) {
		this.records = records;
	}
}