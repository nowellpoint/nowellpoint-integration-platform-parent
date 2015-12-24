package com.nowellpoint.aws.model.sforce;

public class ErrorResponse {
	
	private String message;
	
	private String errorCode;
	
	public ErrorResponse() {
		
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}