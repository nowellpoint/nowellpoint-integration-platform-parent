package com.nowellpoint.aws.api.exception;

import java.io.Serializable;

public class ExceptionResponse implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 6220062845754274884L;
	
	private String message;
	
	public ExceptionResponse() {
		
	}
	
	public ExceptionResponse(String message) {
		setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}	
}