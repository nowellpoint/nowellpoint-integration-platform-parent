package com.nowellpoint.aws.api.tasks;

public class AccountSetupRequest {
	
	private String givenName;
	private String middleName;
	private String surname;
	private String email;
	private String username;
	private String password;
	private String href;

	public AccountSetupRequest() {
		
	}

	public String getGivenName() {
		return givenName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getSurname() {
		return surname;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getHref() {
		return href;
	}

	public AccountSetupRequest withGivenName(String givenName) {
		this.givenName = givenName;
		return this;
	}
	
	public AccountSetupRequest withMiddleName(String middleName) {
		this.middleName = middleName;
		return this;
	}
	
	public AccountSetupRequest withSurname(String surname) {
		this.surname = surname;
		return this;
	}
	
	public AccountSetupRequest withEmail(String email) {
		this.email = email;
		return this;
	}
	
	public AccountSetupRequest withUsername(String username) {
		this.username = username;
		return this;
	}
	
	public AccountSetupRequest withPassword(String password) {
		this.password = password;
		return this;
	}
	
	public AccountSetupRequest withHref(String href) {
		this.href = href;
		return this;
	}
}