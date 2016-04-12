package com.nowellpoint.aws.data.mongodb;

public class Connector {
	
	private String displayName;
	
	private String organizationName;
	
	private String id;
	
	public Connector() {
		
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}