package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamingEventFailure {
	
	@Getter @JsonProperty("failure")
	private Failure failure;
	
	public StreamingEventFailure() {
		
	}
}