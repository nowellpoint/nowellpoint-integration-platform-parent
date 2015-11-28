package com.nowellpoint.aws.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractLambdaRequest implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3106185171746863094L;

	public AbstractLambdaRequest() {
		
	}
}