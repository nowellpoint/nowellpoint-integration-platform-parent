package com.nowellpoint.aws.api.service;

import javax.ws.rs.core.Response;

import com.nowellpoint.aws.api.dto.ErrorDTO;

public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3813636762037658612L;
	
	private Response.Status status;
	
	private ErrorDTO error;
	
	private String message;
	
	private Integer code;
	
	public ServiceException(String message) {
		super(message);
		error = new ErrorDTO(null, message);
		this.status = Response.Status.BAD_REQUEST;
	}
	
	public ServiceException(Integer code, String message) {
		super(message);
		error = new ErrorDTO(code, message);
		this.status = Response.Status.BAD_REQUEST;
	}
	
	public ServiceException(ErrorDTO error) {
		super(error.getMessage());
		this.error = error;
		this.status = Response.Status.BAD_REQUEST;
	}
	
	public ServiceException(Response.Status status, String message) {
		super(message);
		this.error = new ErrorDTO(null, message);
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
	
	public Response.Status getStatus() {
		return status;
	}
	
	public ErrorDTO getError() {
		return error;
	}
}