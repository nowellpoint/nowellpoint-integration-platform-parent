package com.nowellpoint.api.model.document;

public class Schedule {
	
	private String environmentName;
	
	private String environmentKey;
	
	private Integer hour;
	
	private Integer minute;
	
	private Integer second;
	
	private String status;
	
	public Schedule() {
		
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public String getEnvironmentKey() {
		return environmentKey;
	}

	public void setEnvironmentKey(String environmentKey) {
		this.environmentKey = environmentKey;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}
	
	public Integer getHour() {
		return hour;
	}
	
	public void setMinute(Integer minute) {
		this.minute = minute;
	}
	
	public Integer getMinute() {
		return minute;
	}
	
	public void setSecond(Integer second) {
		this.second = second;
	}
	
	public Integer getSecond() {
		return second;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}