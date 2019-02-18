package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AggregateResult implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3464151927063768439L;

	private Attributes attributes;
	
	private Long expr0;
	
	public AggregateResult() {
		
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public Long getExpr0() {
		return expr0;
	}
}