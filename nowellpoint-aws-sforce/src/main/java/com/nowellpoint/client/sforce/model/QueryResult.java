package com.nowellpoint.client.sforce.model;

import java.util.List;

public class QueryResult<T> {
	
	private Integer totalSize;
	
	private Boolean done;
	
	private List<T> records;
	
	public QueryResult() {
		
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

	public List<T> getRecords() {
		return records;
	}

	public void setRecords(List<T> records) {
		this.records = records;
	}
}