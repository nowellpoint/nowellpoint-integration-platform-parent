package com.nowellpoint.aws.model.data;

public class DeleteDocumentRequest extends AbstractDocumentRequest {
	
	private static final long serialVersionUID = -6090910215991875135L;
	
	private String id;
	
	public DeleteDocumentRequest() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
	
	public DeleteDocumentRequest withId(String id) {
		setId(id);
		return this;
	}

	public DeleteDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
	
	public DeleteDocumentRequest withAccountId(String accountId) {
		setAccountId(accountId);
		return this;
	}
}