package com.nowellpoint.api.rest.domain;

public class JobTypeInfo {

	private String id;
	
	private String name;
	
	private String code;
	
	private String description;
	
	private ConnectorTypeOrig connectorTypeOrig;
	
	public JobTypeInfo() {
		
	}
	
	public JobTypeInfo(JobType jobType) {
		setCode(jobType.getCode());
		setConnectorType(jobType.getSource());
		setDescription(jobType.getDescription());
		setId(jobType.getId());
		setName(jobType.getName());
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

	public ConnectorTypeOrig getConnectorType() {
		return connectorTypeOrig;
	}

	public void setConnectorType(ConnectorTypeOrig connectorTypeOrig) {
		this.connectorTypeOrig = connectorTypeOrig;
	}
}