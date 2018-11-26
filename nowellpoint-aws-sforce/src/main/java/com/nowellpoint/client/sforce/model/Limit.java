package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Limit implements Serializable {

	private static final long serialVersionUID = -7491926015649603540L;

	@JsonProperty("Max")
	private Long max;
	
	@JsonProperty("Remaining")
	private Long remaining;
	
	public Limit() {
		
	}

	public Long getMax() {
		return max;
	}

	public Long getRemaining() {
		return remaining;
	}
}