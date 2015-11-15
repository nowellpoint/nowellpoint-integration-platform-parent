package com.nowellpoint.aws.idp.model;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractRequest;

public class GetTokenRequest extends AbstractRequest implements Serializable {

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
		setUsername(username);
		return this;
	}
	
	public GetTokenRequest withPassword(String password) {
		setPassword(password);
		return this;
	}
}