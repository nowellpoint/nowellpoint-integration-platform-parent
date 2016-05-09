package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Error implements Serializable {

	private static final long serialVersionUID = -4556365201148471897L;

	private String error;
	
	@JsonProperty(value="error_description")
	private String errorDescription;
	
	public Error() {
		
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