package com.nowellpoint.aws.data.test;

import java.io.Serializable;

public class Contact implements Serializable {
	
	private static final long serialVersionUID = 9149019017395211164L;

	private String firstName;
	
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
}