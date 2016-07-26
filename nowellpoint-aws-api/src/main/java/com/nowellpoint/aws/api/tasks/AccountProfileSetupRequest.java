package com.nowellpoint.aws.api.tasks;

public class AccountProfileSetupRequest {
	
	private String href;
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	private Boolean isActive;
	private String countryCode;
	
	public AccountProfileSetupRequest() {
		
	}
	
	public String getHref() {
		return href;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public Boolean getIsActive() {
		return isActive;
	}
	
	public String getCountryCode() {
		return countryCode;
	}

	public AccountProfileSetupRequest withHref(String href) {
		this.href = href;
		return this;
	}
	
	public AccountProfileSetupRequest withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}
	
	public AccountProfileSetupRequest withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	
	public AccountProfileSetupRequest withEmail(String email) {
		this.email = email;
		return this;
	}
	
	public AccountProfileSetupRequest withUsername(String username) {
		this.username = username;
		return this;
	}
	
	public AccountProfileSetupRequest withIsActive(Boolean isActive) {
		this.isActive = isActive;
		return this;
	}
	
	public AccountProfileSetupRequest withCountryCode(String countryCode) {
		this.countryCode = countryCode;
		return this;
	}
}