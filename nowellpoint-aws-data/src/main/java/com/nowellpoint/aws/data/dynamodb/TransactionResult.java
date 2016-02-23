package com.nowellpoint.aws.data.dynamodb;

public class TransactionResult {

	public String userId;
	
	public String organizationId;
	
	public TransactionResult() {
		
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
}