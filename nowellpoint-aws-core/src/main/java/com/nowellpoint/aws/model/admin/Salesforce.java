package com.nowellpoint.aws.model.admin;

import java.io.Serializable;

public class Salesforce implements Serializable {
	
	private static final long serialVersionUID = -7077573035663609126L;

	private String username;
	
	private String password;
	
	private String securityToken;
	
	public Salesforce() {
		
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
}