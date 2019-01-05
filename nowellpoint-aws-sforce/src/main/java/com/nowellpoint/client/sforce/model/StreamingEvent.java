package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamingEvent implements Serializable {

	private static final long serialVersionUID = -8426229851500146364L;
	
	@Getter @JsonProperty("event") private Event event;
	@Getter @JsonProperty("sobject") private SObject sobject;
	
	public StreamingEvent() {
		
	}
}