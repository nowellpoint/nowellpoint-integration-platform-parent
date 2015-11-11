package com.nowellpoint.aws.lambda.idp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
   
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdpServiceException implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -7674275110589489002L;
	
	@JsonProperty(value="message")
	private String error;
	
	@JsonProperty(value="developerMessage")
	private String message;
	
	public IdpServiceException() {
		
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