package com.nowellpoint.api.rest.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.nowellpoint.mongodb.document.MongoDocument;

public class Dashboard extends AbstractResource {

	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;

	private Integer connectors;
	
	private Dashboard() {
		
	}
	
	private Dashboard(String id) {
		setId(id);
	}
	
	private <T> Dashboard(T document) {
		modelMapper.map(document, this);
	}
	
	public static Dashboard of(String id) {
		return new Dashboard(id);
	}
	
	public static Dashboard of(MongoDocument document) {
		return new Dashboard(document);
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
	
	public Integer getConnectors() {
		return connectors;
	}

	public void setConnectors(Integer connectors) {
		this.connectors = connectors;
	}

	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.AccountProfile.class);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}