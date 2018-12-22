package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamingEvent implements Serializable {

	private static final long serialVersionUID = -8426229851500146364L;
	
	@JsonProperty("event")
	private Event event;
	
	@JsonProperty("sobject")
	private SObject sobject;
	
	public StreamingEvent() {
		
	}

	public Event getEvent() {
		return event;
	}

	public SObject getSObject() {
		return sobject;
	}
}