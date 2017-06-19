package com.nowellpoint.client.model;

public class JobStatusAggregation {
	
	private String status;
	
	private Long count;
	
	public JobStatusAggregation() {
		
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