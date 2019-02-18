package com.nowellpoint.client.sforce.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event implements Serializable {

	private static final long serialVersionUID = -2457215427134346117L;

	@JsonProperty("createdDate")
	private Date createdDate;
	
	@JsonProperty("replayId")
	private Long replayId;
	
	@JsonProperty("type")
	private String type;
	
	public Event() {
		
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Long getReplayId() {
		return replayId;
	}

	public String getType() {
		return type;
	}
}