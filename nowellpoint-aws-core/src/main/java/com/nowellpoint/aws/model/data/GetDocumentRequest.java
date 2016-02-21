package com.nowellpoint.aws.model.data;

import org.bson.types.ObjectId;

public class GetDocumentRequest extends AbstractDocumentRequest {

	private static final long serialVersionUID = -8063355241890824157L;
	
	private ObjectId id;
	
	public GetDocumentRequest() {
		
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
	
	public GetDocumentRequest withMongoDBConnectUri(String mongoDBConnectUri) {
		setMongoDBConnectUri(mongoDBConnectUri);
		return this;
	}
	
	public GetDocumentRequest withId(ObjectId id) {
		setId(id);
		return this;
	}

	public GetDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
}