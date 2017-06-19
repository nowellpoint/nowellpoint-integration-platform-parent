package com.nowellpoint.api.rest.domain;

public class JobStatusAggregation {
	
	private String status;
	
	private Long count;
	
	private JobStatusAggregation(String status, Long count) {
		this.status = status;
		this.count = count;
	}
	
	public static JobStatusAggregation of(String status, Long count) {
		return new JobStatusAggregation(status, count);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}