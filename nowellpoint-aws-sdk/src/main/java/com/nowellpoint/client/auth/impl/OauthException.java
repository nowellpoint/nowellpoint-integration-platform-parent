package com.nowellpoint.client.auth.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OauthException extends RuntimeException {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5043817691692313615L;

	@JsonProperty(value="error")
	private String error;
	
	@JsonProperty(value="error_description")
	private String errorDescription;
	
	public OauthException(String error, String errorDescription) {
		super();
		this.error = error;
		this.errorDescription = errorDescription;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
}
