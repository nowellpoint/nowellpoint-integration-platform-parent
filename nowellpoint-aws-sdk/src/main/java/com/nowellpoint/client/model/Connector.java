package com.nowellpoint.client.model;

import java.util.Date;

public class Connector extends AbstractResource {
	
	private String name;
	private String typeName;
	private String authEndpoint;
	private String connectionStatus;
	private Date connectionDate;
	private Boolean isConnected;
	private OrganizationInfo owner;
	
	public Connector() {
		
	}

	public String getName() {
		return name;
	}

	public String getAuthEndpoint() {
		return authEndpoint;
	}

	public String getTypeName() {
		return typeName;
	}

	public OrganizationInfo getOwner() {
		return owner;
	}

	public String getConnectionStatus() {
		return connectionStatus;
	}

	public Date getConnectionDate() {
		return connectionDate;
	}
	
	public Boolean getIsConnected() {
		return isConnected;
	}
}