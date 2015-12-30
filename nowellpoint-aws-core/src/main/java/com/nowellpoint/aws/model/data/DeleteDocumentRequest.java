package com.nowellpoint.aws.model.data;

public class DeleteDocumentRequest extends AbstractDocumentRequest {
	
	private static final long serialVersionUID = -6090910215991875135L;
	
	public DeleteDocumentRequest() {
		
	}

	public String getId() {
		return super.getId();
	}

	public void setId(String id) {
		super.setId(id);
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
	
	public DeleteDocumentRequest withId(String id) {
		setId(id);
		return this;
	}

	public DeleteDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
}