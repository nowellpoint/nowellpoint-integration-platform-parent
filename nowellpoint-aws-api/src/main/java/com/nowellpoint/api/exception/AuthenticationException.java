package com.nowellpoint.api.exception;

public class AuthenticationException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -199984852210399810L;
	
	private String error;
	
	private String errorDescription;
	
	public AuthenticationException(String error, String errorDescription) {
		super();
		this.error = error;
		this.errorDescription = errorDescription;
	}

	public String getError() {
		return error;
	}

	public String getErrorDescription() {
		return errorDescription;
	}
}