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

	public String getLastName() {
		return lastName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public Date getAddedOn() {
		return addedOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}
}