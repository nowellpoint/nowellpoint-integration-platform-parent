package com.nowellpoint.api.dto;

public class ErrorDTO {
	
	private Integer code;
	
	private String message;
	
	public ErrorDTO() {
		
	}
	
	public ErrorDTO(Integer code, String message) {
		setCode(code);
		setMessage(message);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}