package com.nowellpoint.aws.model;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractRequest implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3106185171746863094L;

	public AbstractRequest() {
		
	}

	public String getAsJson() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this).toString();
	}
}