package com.nowellpoint.api.rest.domain;

import com.nowellpoint.mongodb.document.MongoDocument;

public class JobType extends AbstractResource {
	
	private Meta meta;
	
	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;
	
	private String name;
	
	private String code;
	
	private String description;
	
	private String languageSidKey;
	
	private ConnectorType connectorType;
	
	public JobType() {
		
	}
	
	private <T> JobType(T document) {
		modelMapper.map(document, this);
	}
	
	public static JobType of(MongoDocument document) {
		return new JobType(document);
	}
	
	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public UserInfo getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserInfo createdBy) {
		this.createdBy = createdBy;
	}

	public UserInfo getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserInfo lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
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
		return modelMapper.map(this, com.nowellpoint.api.model.document.JobType.class);
	}
}