package com.nowellpoint.signup.entity;

import java.net.URI;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "registrations", noClassnameStored = true)
public class RegistrationDocument extends BaseEntity {
	
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String countryCode;
	private String emailVerificationToken;
	private String domain;
	private URI emailVerificationHref;
	private Long expiresAt;
	private String planId;
	private String identityHref; 
	//public abstract UserInfo getCreatedBy();
	//public abstract UserInfo getLastUpdatedBy();
	private Boolean verified;
	
	public RegistrationDocument() {
		
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

	public URI getEmailVerificationHref() {
		return emailVerificationHref;
	}

	public void setEmailVerificationHref(URI emailVerificationHref) {
		this.emailVerificationHref = emailVerificationHref;
	}

	public Long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getIdentityHref() {
		return identityHref;
	}

	public void setIdentityHref(String identityHref) {
		this.identityHref = identityHref;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}
}