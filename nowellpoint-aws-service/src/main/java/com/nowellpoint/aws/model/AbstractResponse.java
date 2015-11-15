package com.nowellpoint.aws.model;

import java.io.Serializable;

public class AbstractResponse implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -819250358735378538L;
	
	private Integer statusCode;
	
	private String errorCode;
	
	private String errorMessage;
	
	public AbstractResponse() {
		
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}