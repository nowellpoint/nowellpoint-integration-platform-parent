package com.nowellpoint.sdk.salesforce.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QueryResult {
	
	private Integer totalSize;
	
	private Boolean done;
	
	private JsonNode[] records;
	
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

	public JsonNode[] getRecords() {
		return records;
	}

	public void setRecords(JsonNode[] records) {
		this.records = records;
	}
	
	public <T> Set<T> getRecords(Class<T> valueType) {
		ObjectMapper objectMapper = new ObjectMapper();
		return Arrays.asList(records).stream()
				.map(r -> {
					try {
						return objectMapper.readValue(r.toString(), valueType);
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
		})
		.collect(Collectors.toSet());
	}
}