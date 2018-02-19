package com.nowellpoint.client.model;

public class Error {
	
	private String code;
	
	private String[] messages;
	
	public Error() {
		
	}

	public String getCode() {
		return code;
	}

	public String[] getMessages() {
		return messages;
	}
	
	public String getErrorMessage() {
		return messages[0];
	}
 }