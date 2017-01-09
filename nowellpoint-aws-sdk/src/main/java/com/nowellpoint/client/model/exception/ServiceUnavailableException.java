package com.nowellpoint.client.model.exception;

public class ServiceUnavailableException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -5244356399146726790L;
	
	public ServiceUnavailableException(String message) {
		super(message);
	}
}