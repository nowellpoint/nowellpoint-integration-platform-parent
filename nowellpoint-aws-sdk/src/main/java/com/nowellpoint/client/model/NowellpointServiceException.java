package com.nowellpoint.client.model;

public class NowellpointServiceException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3614890910375988952L;
	
	private int statusCode;
	
	private String message;
	
	public NowellpointServiceException(int statusCode, String message) {
		super();
		this.statusCode = statusCode;
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}
	
	public String getMessage() {
		return message;
	}
}
