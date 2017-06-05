package com.nowellpoint.client.model.exception;

public class ForbiddenException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4018757693125807951L;
	
	public ForbiddenException(String message) {
		super(message);
	}
}