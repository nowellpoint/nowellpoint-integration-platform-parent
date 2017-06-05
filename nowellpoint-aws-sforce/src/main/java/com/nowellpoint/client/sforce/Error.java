package com.nowellpoint.client.sforce;

public class Error {
	
	private String message;
	
	private String errorCode;
	
	public Error() {
		
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