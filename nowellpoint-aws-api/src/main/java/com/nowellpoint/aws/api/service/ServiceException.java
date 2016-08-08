package com.nowellpoint.aws.api.service;

import com.nowellpoint.aws.api.dto.ErrorDTO;

public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3813636762037658612L;
	
	private ErrorDTO error;
	
	private String message;
	
	private Integer code;
	
	public ServiceException(String message) {
		super(message);
		error = new ErrorDTO(null, message);
	}
	
	public ServiceException(Integer code, String message) {
		super(message);
		error = new ErrorDTO(code, message);
	}
	
	public ServiceException(ErrorDTO error) {
		super();
		this.error = error;
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
	
	public ErrorDTO getError() {
		return error;
	}
}