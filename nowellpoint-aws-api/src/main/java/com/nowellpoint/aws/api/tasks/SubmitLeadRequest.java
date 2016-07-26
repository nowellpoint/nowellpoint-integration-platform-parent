package com.nowellpoint.aws.api.tasks;

public class SubmitLeadRequest {
	
	private String leadSource;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String company;
	private String description;
	private String countryCode;
	
	public SubmitLeadRequest() {
		
	}

	public String getLeadSource() {
		return leadSource;
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

	public String getPhone() {
		return phone;
	}

	public String getCompany() {
		return company;
	}

	public String getDescription() {
		return description;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public SubmitLeadRequest withLeadSource(String leadSource) {
		this.leadSource = leadSource;
		return this;
	}
	
	public SubmitLeadRequest withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}
	
	public SubmitLeadRequest withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	
	public SubmitLeadRequest withEmail(String email) {
		this.email = email;
		return this;
	}
	
	public SubmitLeadRequest withPhone(String phone) {
		this.phone = phone;
		return this;
	}
	
	public SubmitLeadRequest withCompany(String company) {
		this.company = company;
		return this;
	}
	
	public SubmitLeadRequest withDescription(String description) {
		this.description = description;
		return this;
	}
	
	public SubmitLeadRequest withCountryCode(String countryCode) {
		this.countryCode = countryCode;
		return this;
	}
}