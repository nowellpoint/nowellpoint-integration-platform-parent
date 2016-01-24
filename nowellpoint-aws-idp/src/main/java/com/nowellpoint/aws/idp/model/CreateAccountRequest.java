package com.nowellpoint.aws.idp.model;

import org.hibernate.validator.constraints.NotEmpty;

public class CreateAccountRequest extends AbstractIdpRequest {

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
	
	private String password;
	
	private String status;
	
	public CreateAccountRequest() {
		setStatus("UNVERIFIED");
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

	@NotEmpty
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@NotEmpty
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

	@NotEmpty
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	@NotEmpty
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public CreateAccountRequest withApiKeyId(String apiKeyId) {
		setApiKeyId(apiKeyId);
		return this;
	}
	
	public CreateAccountRequest withApiKeySecret(String apiKeySecret) {
		setApiKeySecret(apiKeySecret);
		return this;
	}
	
	public CreateAccountRequest withDirectoryId(String directoryId) {
		setDirectoryId(directoryId);
		return this;
	}
	
	public CreateAccountRequest withApiEndpoint(String apiEndpoint) {
		setApiEndpoint(apiEndpoint);
		return this;
	}
	
	public CreateAccountRequest withUsername(String username) {
		setUsername(username);
		return this;
	}
	
	public CreateAccountRequest withEmail(String email) {
		setEmail(email);
		return this;
	}
	
	public CreateAccountRequest withGivenName(String givenName) {
		setGivenName(givenName);
		return this;
	}
	
	public CreateAccountRequest withMiddleName(String middleName) {
		setMiddleName(middleName);
		return this;
	}
	
	public CreateAccountRequest withSurname(String surname) {
		setSurname(surname);
		return this;
	}
	
	public CreateAccountRequest withPassword(String password) {
		setPassword(password);
		return this;
	}
	
	public CreateAccountRequest withStatus(String status) {
		setStatus(status);
		return this;
	}
}