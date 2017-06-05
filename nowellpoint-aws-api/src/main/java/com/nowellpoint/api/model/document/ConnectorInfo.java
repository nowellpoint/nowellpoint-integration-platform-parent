package com.nowellpoint.api.model.document;

import java.io.Serializable;

import com.nowellpoint.mongodb.annotation.EmbedOne;

public class ConnectorInfo implements Serializable {

	private static final long serialVersionUID = -7419796858368941784L;

	private String id;
	
	private String name;
	
	private String organizationName;
	
	private String serverName;
	
	@EmbedOne
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