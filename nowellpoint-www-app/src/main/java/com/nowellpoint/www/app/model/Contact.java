package com.nowellpoint.www.app.model;

public class Contact {
	
	/**
	 * 
	 */
	
	private String firstName;
	
	/**
	 * 
	 */
	
	private String lastName;
	
	public Contact() {
		
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
	
	public Contact withFirstName(String firstName) {
		setFirstName(firstName);
		return this;
	}
	
	public Contact withLastName(String lastName) {
		setLastName(lastName);
		return this;
	}
}