package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

public class Failure {
	
	@Getter @JsonProperty("exception") private String exception;
	@Getter @JsonProperty("channel") private String channel;
	@Getter @JsonProperty("id") private Long id;
	@Getter @JsonProperty("successful") private Boolean successful;

	public Failure() {
		
	}
}