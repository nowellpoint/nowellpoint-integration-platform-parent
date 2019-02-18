package com.nowellpoint.sdk.salesforce.model;

import java.util.List;

public class ApexClassResult {
	private Long totalSize;
	private Boolean done;
	private List<ApexClass> records;
	
	public ApexClassResult() {
		
	}

	public Long getTotalSize() {
		return totalSize;
	}

	public Boolean getDone() {
		return done;
	}

	public List<ApexClass> getRecords() {
		return records;
	}
}