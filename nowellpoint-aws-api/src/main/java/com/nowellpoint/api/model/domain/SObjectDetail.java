package com.nowellpoint.api.model.domain;

import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;

public class SObjectDetail extends AbstractResource {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3740458498780531334L;
	
	private UserInfo createdBy;
	
	private UserInfo lastModifiedBy;
	
	private String environmentKey;
	
	private Long totalSize;
	
	private String name;
	
	private DescribeSobjectResult result;
	
	public SObjectDetail() {
		
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
}