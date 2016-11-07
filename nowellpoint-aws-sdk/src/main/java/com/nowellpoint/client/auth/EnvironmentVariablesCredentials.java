package com.nowellpoint.client.auth;

public class EnvironmentVariablesCredentials implements UsernamePasswordCredentials {
	
	private String username;
	private String password;
	
	public EnvironmentVariablesCredentials() {
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
