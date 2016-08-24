package com.nowellpoint.aws.api.model;

import java.io.Serializable;

public class Schedule implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 3567625965022977944L;

	private String key;
	
	private String environmentKey;
	
	private Integer hour;
	
	private Integer minute;
	
	private Integer second;
	
	public Schedule() {
		
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
}