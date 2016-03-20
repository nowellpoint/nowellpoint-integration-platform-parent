package com.nowellpoint.www.app.model;

public class ExceptionResponse {
	
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