package com.nowellpoint.aws.lambda.idp.model;

import java.io.Serializable;

public class GetTokenRequest implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5687820017409617494L;
	
	private String username;
	
	private String password;

	public GetTokenRequest() {
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public GetTokenRequest withUsername(String username) {
		this.username = username;
		return this;
	}
	
	public GetTokenRequest withPassword(String password) {
		this.password = password;
		return this;
	}
}