package com.nowellpoint.api.rest.domain;

public class ConnectorInfo {

	private String id;
	
	private String name;
	
	private String organizationName;
	
	private String serverName;
	
	private InstanceInfo instance;
	
	public ConnectorInfo() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public InstanceInfo getInstance() {
		return instance;
	}

	public void setInstance(InstanceInfo instance) {
		this.instance = instance;
	}
}