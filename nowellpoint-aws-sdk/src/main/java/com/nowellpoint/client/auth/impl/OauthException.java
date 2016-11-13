package com.nowellpoint.client.auth.impl;

public class OauthException extends RuntimeException {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5043817691692313615L;

	private Integer code;
	
	private String message;
	
	public OauthException(Integer code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}
}
