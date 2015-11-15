package com.nowellpoint.aws.model;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractRequest implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3106185171746863094L;

	public AbstractRequest() {
		
	}
	
	@JsonIgnore
	public String getAsJson() throws IOException {
		return new ObjectMapper().writeValueAsString(this);
	}
}