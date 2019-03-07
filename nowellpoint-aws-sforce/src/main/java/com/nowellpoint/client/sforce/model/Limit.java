package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Limit implements Serializable {

	private static final long serialVersionUID = -7491926015649603540L;

	@JsonProperty("Max")
	private Integer max;
	
	@JsonProperty("Remaining")
	private Integer remaining;
	
	public Limit() {
		
	}

	public Integer getMax() {
		return max;
	}

	public Integer getRemaining() {
		return remaining;
	}
}