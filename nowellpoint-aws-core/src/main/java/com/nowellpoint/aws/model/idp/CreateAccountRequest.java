package com.nowellpoint.aws.model.idp;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class CreateAccountRequest extends AbstractLambdaRequest {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 350380797564170812L;

	private String apiKeyId;
	
	private String apiKeySecret;
	
	private String username;
	
	private String email;
	
	private String givenName;
	
	private String middleName;
	
	private String surname;
	
	public CreateAccountRequest() {
		
	}

	public String getApiKeyId() {
		return apiKeyId;
	}

	public void setApiKeyId(String apiKeyId) {
		this.apiKeyId = apiKeyId;
	}

	public String getApiKeySecret() {
		return apiKeySecret;
	}

	public void setApiKeySecret(String apiKeySecret) {
		this.apiKeySecret = apiKeySecret;
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

	public CreateAccountRequest withApiKeyId(String apiKeyId) {
		setApiKeyId(apiKeyId);
		return this;
	}
	
	public CreateAccountRequest withApiKeySecret(String apiKeySecret) {
		setApiKeySecret(apiKeySecret);
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
}