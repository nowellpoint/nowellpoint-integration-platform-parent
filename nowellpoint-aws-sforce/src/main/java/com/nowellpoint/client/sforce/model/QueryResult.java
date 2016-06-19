package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.databind.node.ArrayNode;

public class QueryResult {
	
	private Integer totalSize;
	
	private Boolean done;
	
	private ArrayNode records;
	
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

	public ArrayNode getRecords() {
		return records;
	}

	public void setRecords(ArrayNode records) {
		this.records = records;
	}
}