package com.nowellpoint.aws.idp.model;

import org.hibernate.validator.constraints.NotEmpty;

public class SearchAccountRequest extends AbstractIdpRequest {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 350380797564170812L;
	
	private String directoryId;
	
	private String username;
	
	private String email;
	
	private String givenName;
	
	private String middleName;
	
	private String surname;
	
	public SearchAccountRequest() {
		
	}
	
	@NotEmpty
	public String getApiEndpoint() {
		return super.getApiEndpoint();
	}
	
	public void setApiEndpoint(String apiEndpoint) {
		super.setApiEndpoint(apiEndpoint);
	}
	
	@NotEmpty
	public String getDirectoryId() {
		return directoryId;
	}
	
	public void setDirectoryId(String directoryId) {
		this.directoryId = directoryId;
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
	
	public SearchAccountRequest withDirectoryId(String directoryId) {
		setDirectoryId(directoryId);
		return this;
	}
	
	public SearchAccountRequest withApiEndpoint(String apiEndpoint) {
		setApiEndpoint(apiEndpoint);
		return this;
	}
	
	public SearchAccountRequest withApiKeyId(String apiKeyId) {
		setApiKeyId(apiKeyId);
		return this;
	}
	
	public SearchAccountRequest withApiKeySecret(String apiKeySecret) {
		setApiKeySecret(apiKeySecret);
		return this;
	}

	public SearchAccountRequest withUsername(String username) {
		setUsername(username);
		return this;
	}
	
	public SearchAccountRequest withEmail(String email) {
		setEmail(email);
		return this;
	}
	
	public SearchAccountRequest withGivenName(String givenName) {
		setGivenName(givenName);
		return this;
	}
	
	public SearchAccountRequest withMiddleName(String middleName) {
		setMiddleName(middleName);
		return this;
	}
	
	public SearchAccountRequest withSurname(String surname) {
		setSurname(surname);
		return this;
	}
}