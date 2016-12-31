package com.nowellpoint.client.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 4359339056490011294L;
	
	private String href;
	
	private String emailVerificationToken;
	
	private String emailVerificationTokenHref;
	  
	public User() {
		
	}
	
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getEmailVerificationToken() {
		return emailVerificationToken;
	}

	public void setEmailVerificationToken(String emailVerificationToken) {
		this.emailVerificationToken = emailVerificationToken;
	}

	public String getEmailVerificationTokenHref() {
		return emailVerificationTokenHref;
	}

	public void setEmailVerificationTokenHref(String emailVerificationTokenHref) {
		this.emailVerificationTokenHref = emailVerificationTokenHref;
	}
}