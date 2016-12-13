package com.nowellpoint.api.model.document;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.api.model.codec.SObjectDetailCodec;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.mongodb.document.ObjectIdDeserializer;
import com.nowellpoint.mongodb.document.ObjectIdSerializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collectionName="sobject.details", codec=SObjectDetailCodec.class)
public class SObjectDetail extends MongoDocument {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8161621113389201360L;
	
	private UserRef createdBy;
	
	private UserRef lastModifiedBy;
	
	@JsonSerialize(using = ObjectIdSerializer.class)
	@JsonDeserialize(using = ObjectIdDeserializer.class)
	private ObjectId connectorId;
	
	private String environmentKey;
	
	private String name;
	
	private Long totalSize;
	
	private DescribeSobjectResult result;
	
	public SObjectDetail() {
		
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