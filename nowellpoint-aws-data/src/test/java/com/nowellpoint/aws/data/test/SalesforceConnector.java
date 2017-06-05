package com.nowellpoint.aws.data.test;

import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.annotation.EmbedOne;
import com.nowellpoint.mongodb.annotation.Reference;
import com.nowellpoint.mongodb.document.MongoDocument;

@Document(collectionName="salesforce.connectors")
public class SalesforceConnector extends MongoDocument {

	private static final long serialVersionUID = -3438714915624952119L;
	
	@EmbedOne
	private Meta meta;
	
	private String name;
	
	@Reference(referenceClass = AccountProfile.class)
	private UserInfo createdBy;
	
	@Reference(referenceClass = AccountProfile.class)
	private UserInfo lastUpdatedBy;
	
	public SalesforceConnector() {
		
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
}