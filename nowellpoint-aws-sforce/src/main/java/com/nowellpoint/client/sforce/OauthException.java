package com.nowellpoint.client.sforce;

import com.nowellpoint.client.sforce.model.Error;

public class OauthException extends RuntimeException {
	
	private static final long serialVersionUID = 2726007023971974319L;
	
	private int statusCode;
	
	private String error;
	
	private String errorDescription;

	public OauthException(int statusCode, Error error) {
		super(error.getErrorDescription());
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