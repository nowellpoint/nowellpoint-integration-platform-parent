package com.nowellpoint.client.model;

public class SalesforceMetadata {
	private String organizationId;
	private String instanceName;
	private String organizationName;
	private String serviceEndpoint;
	
	public SalesforceMetadata() {
		
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}
}