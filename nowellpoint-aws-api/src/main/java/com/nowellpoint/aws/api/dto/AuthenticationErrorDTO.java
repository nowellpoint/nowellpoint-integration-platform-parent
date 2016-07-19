package com.nowellpoint.aws.api.dto;

public class AuthenticationErrorDTO {
	
	public Integer code;
	
	public String message;
	
	public AuthenticationErrorDTO() {
		
	}
	
	public AuthenticationErrorDTO(Integer code, String message) {
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