package com.nowellpoint.client.model.exception;

public class NotFoundException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -7987280636789683575L;
	
	public NotFoundException(String message) {
		super(message);
	}
}