package com.nowellpoint.api.model.document;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 9149019017395211164L;

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
}