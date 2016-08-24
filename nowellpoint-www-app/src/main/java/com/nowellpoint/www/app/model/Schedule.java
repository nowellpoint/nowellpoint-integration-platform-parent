package com.nowellpoint.www.app.model;

public class Schedule {
	
	private String key;
	
	private String environmentKey;
	
	private Integer hour;
	
	private Integer minute;
	
	private Integer second;
	
	public Schedule() {
		
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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
}