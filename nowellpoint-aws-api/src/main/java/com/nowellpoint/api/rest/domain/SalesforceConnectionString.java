package com.nowellpoint.api.rest.domain;

import java.util.Arrays;

public class SalesforceConnectionString {
	
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String PASSWORD = "password";
	private String scheme;
	private String credentials;
	private String hostname;
	private String organizationId;
	private String userId;
	private String grantType;
	
	private SalesforceConnectionString(String connectString) {
		scheme = connectString.substring(0, connectString.indexOf("://"));
		connectString = connectString.replace(scheme.concat("://"), "");
		credentials = connectString.substring(0, connectString.indexOf("@"));
		connectString = connectString.replace(credentials.concat("@"), "");
		hostname = connectString.substring(0, connectString.indexOf("/id/"));
		connectString = connectString.replace(hostname.concat("/id/"), "");
		organizationId = connectString.substring(0, connectString.indexOf("/"));
		connectString = connectString.replace(organizationId.concat("/"), "");
		userId = connectString.substring(0, connectString.indexOf("?"));
		connectString = connectString.replace(userId.concat("?"), "");
		String[] params = connectString.split("&");
		
		Arrays.asList(params).stream().forEach(param -> {
			String[] nameValuePair = param.split("=");
			if ("grant_type".equals(nameValuePair[0])) {
				grantType = nameValuePair[1];
			}
		});
	}
	
	public static SalesforceConnectionString of(String connectionString) {
		return new SalesforceConnectionString(connectionString);
	}
	
	public static SalesforceConnectionString of (ConnectionString connectionString) {
		return new SalesforceConnectionString(connectionString.get());
	}
	
	public String getGrantType() {
		return grantType;
	}
	
	public Boolean isSandbox() {
		return false;
	}
	
	public String getOrganizationId() {
		return organizationId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getCredentials() {
		return credentials;
	}
	
	public String getHostname() {
		return hostname;
	}
}