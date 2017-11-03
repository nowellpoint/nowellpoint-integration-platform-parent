package com.nowellpoint.client.model;

import java.util.Date;

public class Contact {
	
	private String firstName;
	
	private String lastName;
	
	private String name;
	
	private String email;
	
	private String phone;
	
	private Date addedOn;
	
	private Date updatedOn;
	
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
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public Date getAddedOn() {
		return addedOn;
	}

	public void setAddedOn(Date addedOn) {
		this.addedOn = addedOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Contact withFirstName(String firstName) {
		setFirstName(firstName);
		return this;
	}
	
	public Contact withLastName(String lastName) {
		setLastName(lastName);
		return this;
	}
	
	public Contact withEmail(String email) {
		setEmail(email);
		return this;
	}
	
	public Contact withPhone(String phone) {
		setPhone(phone);
		return this;
	}
}