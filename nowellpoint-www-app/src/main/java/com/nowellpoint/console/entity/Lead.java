package com.nowellpoint.console.entity;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "leads")
public class Lead extends BaseEntity {

	private static final long serialVersionUID = 5038204198098590206L;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String message;
	
	public Lead() {
		
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}