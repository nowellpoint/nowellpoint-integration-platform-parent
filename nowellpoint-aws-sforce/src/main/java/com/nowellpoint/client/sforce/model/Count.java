package com.nowellpoint.client.sforce.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Count {

	private Long totalSize;
	
	private Boolean done;
	
	private List<AggregateResult> records;
	
	public Count() {
		
	}

	public Long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Long totalSize) {
		this.totalSize = totalSize;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public List<AggregateResult> getRecords() {
		return records;
	}

	public void setRecords(List<AggregateResult> records) {
		this.records = records;
	}
}