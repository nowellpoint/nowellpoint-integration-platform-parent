package com.nowellpoint.api.rest.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class Error {
	
	private Integer code;
	
	private String message;
	
	private String[] messages;
	
	public Error() {
		
	}
	
	public Error(Integer code, String message) {
		setCode(code);
		setMessage(message);
	}
	
	public Error(Integer code, String[] messages) {
		this.messages = messages;
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
	
	public String[] getMessages() {
		return messages;
	}
	
	public void setMessages(String[] messages) {
		this.messages = messages;
	}
}