package com.nowellpoint.client.sforce.impl;

import com.nowellpoint.client.sforce.model.Error;

public class SalesforceException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	private int statusCode;
	
	private String error;
	
	private String errorDescription;

	public SalesforceException(int statusCode, Error error) {
		super();
		this.statusCode = statusCode;
		this.error = error.getError();
		this.errorDescription = error.getErrorDescription();
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getError() {
		return error;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

}
