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

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public Long getExpr0() {
		return expr0;
	}

	public void setExpr0(Long expr0) {
		this.expr0 = expr0;
	}
}