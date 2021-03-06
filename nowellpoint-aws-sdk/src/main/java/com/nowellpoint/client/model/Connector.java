package com.nowellpoint.client.model;

import java.util.Date;
import java.util.List;

public class Connector extends AbstractResource {
	
	private String name;
	private String typeName;
	private String authEndpoint;
	private String status;
	private Date connectedOn;
	private Boolean isConnected;
	private OrganizationInfo owner;
	private String iconHref;
	private String connectedAs;
	private String username;
	private String clientId;
	private Boolean isSandbox;
	private SalesforceMetadata salesforceMetadata;
	private List<Service> services;
	
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

	public String getStatus() {
		return status;
	}
	
	public Date getConnectedOn() {
		return connectedOn;
	}

	public Boolean getIsConnected() {
		return isConnected;
	}
	
	public String getIconHref() {
		return iconHref;
	}
	
	public String getConnectedAs() {
		return connectedAs;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public Boolean getIsSandbox() {
		return isSandbox;
	}

	public SalesforceMetadata getSalesforceMetadata() {
		return salesforceMetadata;
	}
	
	public List<Service> getServices() {
		return services;
	}
}