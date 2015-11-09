package com.nowellpoint.aws.lambda.idp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdpException implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -7674275110589489002L;
	
	@JsonProperty(value="error")
	private String error;
	
	@JsonProperty(value="message")
	private String message;
	
	public IdpException() {
		
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}