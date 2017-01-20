package com.nowellpoint.api.model.domain;

import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.mongodb.document.MongoDocument;

public class SObjectDetail extends AbstractResource {
	
	private UserInfo createdBy;
	
	private UserInfo lastModifiedBy;
	
	private String environmentKey;
	
	private Long totalSize;
	
	private String name;
	
	private DescribeSobjectResult result;
	
	public SObjectDetail() {
		
	}
	
	public SObjectDetail(MongoDocument document) {
		super(document);
	}

	public UserInfo getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserInfo createdBy) {
		this.createdBy = createdBy;
	}

	public UserInfo getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(UserInfo lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getEnvironmentKey() {
		return environmentKey;
	}

	public void setEnvironmentKey(String environmentKey) {
		this.environmentKey = environmentKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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