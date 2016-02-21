package com.nowellpoint.aws.model.data;

import org.bson.types.ObjectId;

public class DeleteDocumentRequest extends AbstractDocumentRequest {
	
	private static final long serialVersionUID = -6090910215991875135L;
	
	private ObjectId id;
	
	public DeleteDocumentRequest() {
		
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getCollectionName() {
		return super.getCollectionName();
	}

	public void setCollectionName(String collectionName) {
		super.setCollectionName(collectionName);
	}
	
	public DeleteDocumentRequest withMongoDBConnectUri(String mongoDBConnectUri) {
		setMongoDBConnectUri(mongoDBConnectUri);
		return this;
	}
	
	public DeleteDocumentRequest withId(ObjectId id) {
		setId(id);
		return this;
	}

	public DeleteDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
}