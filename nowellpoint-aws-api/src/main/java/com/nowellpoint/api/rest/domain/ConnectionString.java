package com.nowellpoint.api.rest.domain;

public class ConnectionString {
	
	private String connectionString;
	
	public ConnectionString() {
		
	}
	
	private ConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
	
	public static ConnectionString salesforce(String credentials, String identityId, String grantType) {
		String connectionString = "salesforce://".concat(credentials)
				.concat("@")
				.concat(identityId)
				.concat("?grant_type=")
				.concat(grantType);
		
		return new ConnectionString(connectionString);
	}

	public String get() {
		return connectionString;
	}
}