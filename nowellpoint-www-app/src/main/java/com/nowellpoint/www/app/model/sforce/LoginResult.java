package com.nowellpoint.www.app.model.sforce;

public class LoginResult {
	
	private String id;
	
	private String sessionId;
	
	private String organizationId;
	
	private String organizationName;
	
	private String userId;
	
	private String displayName;
	
	private String userName;
	
	private String authEndpoint;
	
	private String serviceEndpoint;

	public LoginResult() {
		
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAuthEndpoint() {
		return authEndpoint;
	}

	public void setAuthEndpoint(String authEndpoint) {
		this.authEndpoint = authEndpoint;
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(String serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}
	
	public LoginResult withId(String id) {
		setId(id);
		return this;
	}

	public LoginResult withSessionId(String sessionId) {
		setSessionId(sessionId);
		return this;
	}
	
	public LoginResult withOrganizationId(String organizationId) {
		setOrganizationId(organizationId);
		return this;
	}
	
	public LoginResult withOrganziationName(String organziationName) {
		setOrganizationName(organziationName);
		return this;
	}
	
	public LoginResult withUserId(String userId) {
		setUserId(userId);
		return this;
	}
	
	public LoginResult withDisplayName(String displayName) {
		setDisplayName(displayName);
		return this;
	}
	
	public LoginResult withUserName(String username) {
		setUserName(username);
		return this;
	}
	
	public LoginResult withAuthEndpoint(String authEndpoint) {
		setAuthEndpoint(authEndpoint);
		return this;
	}
	
	public LoginResult withServiceEndpoint(String serviceEndpoint) {
		setServiceEndpoint(serviceEndpoint);
		return this;
	}
}