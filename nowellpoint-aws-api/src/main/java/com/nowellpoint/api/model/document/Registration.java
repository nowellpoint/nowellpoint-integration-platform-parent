package com.nowellpoint.api.model.document;

import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.annotation.Reference;
import com.nowellpoint.mongodb.document.MongoDocument;

@Document(collectionName="registrations")
public class Registration extends MongoDocument {

	private static final long serialVersionUID = -1912405794198820540L;
	
	private @Reference(referenceClass = UserProfile.class) UserRef createdBy;
	private @Reference(referenceClass = UserProfile.class) UserRef lastUpdatedBy;
	private String firstName;
	private String lastName;
	private String email;
	private String countryCode;
	private String emailVerificationToken;
	private String domain;
	private Long expiresAt;
	private String identityHref;
	private Subscription subscription;

	public Registration() {
		
	}

	public UserRef getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserRef createdBy) {
		this.createdBy = createdBy;
	}

	public UserRef getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserRef lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getIdentityHref() {
		return identityHref;
	}

	public void setIdentityHref(String identityHref) {
		this.identityHref = identityHref;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}
}
