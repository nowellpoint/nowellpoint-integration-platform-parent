package com.nowellpoint.api.rest.domain;

import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.mongodb.document.MongoDocument;

public class SObjectDetail extends AbstractResource {
	
	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;
	
	private String connectorId;
	
	private Long totalSize;
	
	private DescribeSobjectResult result;
	
	public SObjectDetail() {
		
	}
	
	private <T> SObjectDetail(T document) {
		modelMapper.map(document, this);
	}
	
	public static SObjectDetail of(MongoDocument document) {
		return new SObjectDetail(document);
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

	public String getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(String connectorId) {
		this.connectorId = connectorId;
	}

	public Long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Long totalSize) {
		this.totalSize = totalSize;
	}

	public DescribeSobjectResult getResult() {
		return result;
	}

	public void setResult(DescribeSobjectResult result) {
		this.result = result;
	}
	
	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.SObjectDetail.class);
	}
}