package com.nowellpoint.api.rest.domain;

import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.mongodb.document.MongoDocument;

public class SObjectDetail extends AbstractResource {
	
	private UserInfo createdBy;
	
	private UserInfo lastModifiedBy;
	
	private String instanceKey;
	
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

	public String getInstanceKey() {
		return instanceKey;
	}

	public void setInstanceKey(String instanceKey) {
		this.instanceKey = instanceKey;
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