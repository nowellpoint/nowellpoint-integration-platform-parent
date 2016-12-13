package com.nowellpoint.client.model;

public class SignUpRequest {
	
	private String email;
	
	private String firstName;
	
	private String lastName;
	
	private String password;
	
	private String confirmPassword;
	
	private String countryCode;
	
	public SignUpRequest() {
		
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public SignUpRequest withEmail(String email) {
		setEmail(email);
		return this;
	}
	
	public SignUpRequest withFirstName(String firstName) {
		setFirstName(firstName);
		return this;
	}
	
	public SignUpRequest withLastName(String lastName) {
		setLastName(lastName);
		return this;
	}
	
	public SignUpRequest withPassword(String password) {
		setPassword(password);
		return this;
	}
	
	public SignUpRequest withConfirmPassword(String confirmPassword) {
		setConfirmPassword(confirmPassword);
		return this;
	}
	
	public SignUpRequest withCountryCode(String countryCode) {
		setCountryCode(countryCode);
		return this;
	}
}