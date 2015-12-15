package com.nowellpoint.aws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Lead extends AbstractPayload {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 7199045654991076942L;
	
	private String name;
	private String email;
	private String message;
	
	public Lead() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}