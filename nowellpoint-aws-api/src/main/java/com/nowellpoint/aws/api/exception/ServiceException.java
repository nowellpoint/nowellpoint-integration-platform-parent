package com.nowellpoint.aws.api.exception;

import javax.ws.rs.core.Response.Status;

public class ServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8925608725328845235L;
	
	private String message;
	
	private Status status;
	
	private Integer statusCode;
	
	public ServiceException(String message, Status status) {
		this.message = message;
		this.status = status;
	}
	
	public ServiceException(String message, Integer statusCode) {
		this.message = message;
		this.statusCode = statusCode;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Status getStatus() {
		if (status == null) {
			status = Status.fromStatusCode(statusCode);
		}
		return status;
	}
	
	public Integer getStatusCode() {
		return statusCode;
	}
}