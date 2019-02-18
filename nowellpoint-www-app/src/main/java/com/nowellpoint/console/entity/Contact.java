package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Date;

public class Contact implements Serializable {
	
	private static final long serialVersionUID = 7757768228776544041L;

	public String firstName;
	
	public String lastName;
	
	public String email;
	
	public String phone;
	
	public Date addedOn;
	
	public Date updatedOn;

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
}