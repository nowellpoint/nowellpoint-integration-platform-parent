package com.nowellpoint.api.model.domain;

import com.nowellpoint.mongodb.document.MongoDocument;

public class ScheduledJobType extends AbstractResource {
	
	private String name;
	
	private String code;
	
	private String description;
	
	private String languageSidKey;
	
	private ConnectorType connectorType;
	
	public ScheduledJobType() {
		
	}
	
	public ScheduledJobType(MongoDocument document) {
		super(document);
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

	public String getLanguageSidKey() {
		return languageSidKey;
	}

	public void setLanguageSidKey(String languageSidKey) {
		this.languageSidKey = languageSidKey;
	}

	public ConnectorType getConnectorType() {
		return connectorType;
	}

	public void setConnectorType(ConnectorType connectorType) {
		this.connectorType = connectorType;
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.ScheduledJobType.class);
	}
}