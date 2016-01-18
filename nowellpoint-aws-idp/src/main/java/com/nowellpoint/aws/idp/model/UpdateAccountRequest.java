package com.nowellpoint.aws.idp.model;

public class UpdateAccountRequest extends AbstractIdpRequest {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 350380797564170812L;
	
	private String username;
	
	private String email;
	
	private String givenName;
	
	private String middleName;
	
	private String surname;
	
	private String href;
	
	public UpdateAccountRequest() {
		
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public UpdateAccountRequest withApiKeyId(String apiKeyId) {
		setApiKeyId(apiKeyId);
		return this;
	}
	
	public UpdateAccountRequest withApiKeySecret(String apiKeySecret) {
		setApiKeySecret(apiKeySecret);
		return this;
	}
	
	public UpdateAccountRequest withUsername(String username) {
		setUsername(username);
		return this;
	}
	
	public UpdateAccountRequest withEmail(String email) {
		setEmail(email);
		return this;
	}
	
	public UpdateAccountRequest withGivenName(String givenName) {
		setGivenName(givenName);
		return this;
	}
	
	public UpdateAccountRequest withMiddleName(String middleName) {
		setMiddleName(middleName);
		return this;
	}
	
	public UpdateAccountRequest withSurname(String surname) {
		setSurname(surname);
		return this;
	}
	
	public UpdateAccountRequest withHref(String href) {
		setHref(href);
		return this;
	}
}