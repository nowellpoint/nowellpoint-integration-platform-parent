package com.nowellpoint.aws.api.service;

public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3813636762037658612L;
	
	public ServiceException(String message) {
		super(message);
	}
}