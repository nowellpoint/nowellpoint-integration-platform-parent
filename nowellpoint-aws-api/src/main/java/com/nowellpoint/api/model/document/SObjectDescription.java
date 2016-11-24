package com.nowellpoint.api.model.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.api.model.codec.SObjectDescriptionCodec;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.document.MongoDocument;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="sobject.descriptions", codec=SObjectDescriptionCodec.class)
public class SObjectDescription extends MongoDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8161621113389201360L;
	
	private UserRef createdBy;
	
	private UserRef lastModifiedBy;
	
	private String environmentKey;
	
	private String name;
	
	private DescribeSobjectResult result;
	
	public SObjectDescription() {
		
	}

	public UserRef getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserRef createdBy) {
		this.createdBy = createdBy;
	}

	public UserRef getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(UserRef lastModifiedBy) {
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

	public DescribeSobjectResult getResult() {
		return result;
	}

	public void setResult(DescribeSobjectResult result) {
		this.result = result;
	}
}