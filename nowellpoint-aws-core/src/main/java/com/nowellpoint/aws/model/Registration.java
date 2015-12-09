package com.nowellpoint.aws.model;

import java.io.Serializable;

public class Registration implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1686297355289665371L;
	
	private String id;
	private String firstName;
	private String lastName;
	private String email;

	public Registration() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}