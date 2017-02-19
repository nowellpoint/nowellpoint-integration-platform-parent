package com.nowellpoint.api.rest.domain;

import com.nowellpoint.mongodb.annotation.EmbedOne;

public class JobTypeInfo {

	private String id;
	
	private String name;
	
	private String code;
	
	private String description;
	
	@EmbedOne
	private ConnectorType connectorType;
	
	public JobTypeInfo() {
		
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ConnectorType getConnectorType() {
		return connectorType;
	}

	public void setConnectorType(ConnectorType connectorType) {
		this.connectorType = connectorType;
	}
}