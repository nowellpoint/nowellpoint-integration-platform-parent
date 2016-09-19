package com.nowellpoint.client.auth;

public class BasicCredentials implements UsernamePasswordCredentials {
	
	private String username;
	private String password;
	
	public BasicCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
}
