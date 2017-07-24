package com.nowellpoint.api.model.document;

import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.document.MongoDocument;

@Document(collectionName="registrations")
public class Registration extends MongoDocument {

	private static final long serialVersionUID = -1912405794198820540L;
	
	private String firstName;
	private String lastName;
	private String email;
	private String countryCode;
	private String emailVerificationToken;
	private String siteName;

	public Registration() {
		
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getEmailVerificationToken() {
		return emailVerificationToken;
	}

	public void setEmailVerificationToken(String emailVerificationToken) {
		this.emailVerificationToken = emailVerificationToken;
	}
	
	public String getSiteName() {
		return siteName;
	}
	
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
}
