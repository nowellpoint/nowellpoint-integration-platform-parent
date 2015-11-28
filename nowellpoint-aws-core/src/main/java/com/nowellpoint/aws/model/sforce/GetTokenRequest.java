package com.nowellpoint.aws.model.sforce;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class GetTokenRequest extends AbstractLambdaRequest implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 3543119199096532676L;
	
	private String username;
	
	private String password;
	
	private String securityToken;

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
	
	public String getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}
	
	public GetTokenRequest withUsername(String username) {
		setUsername(username);
		return this;
	}
	
	public GetTokenRequest withPassword(String password) {
		setPassword(password);
		return this;
	}

	public GetTokenRequest withSecurityToken(String securityToken) {
		setSecurityToken(securityToken);
		return this;
	}
}