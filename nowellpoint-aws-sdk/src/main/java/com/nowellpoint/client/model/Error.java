package com.nowellpoint.client.model;

public class Error {
	
	private Integer code;
	
	private String[] messages;
	
	public Error() {
		
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String[] getMessages() {
		return messages;
	}

	public void setMessages(String[] messages) {
		this.messages = messages;
	}
	
	public String getErrorMessage() {
		return messages[0];
	}
 }