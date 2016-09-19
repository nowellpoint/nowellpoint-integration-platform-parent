package com.nowellpoint.client.auth;

public class EnvironmentPropertyCredentials implements UsernamePasswordCredentials {
	
	private String username;
	private String password;
	
	public EnvironmentPropertyCredentials() {
		this.username = System.getenv("NOWELLPOINT_USERNAME");
		this.password = System.getenv("NOWELLPOINT_PASSWORD");
	}

	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
}
