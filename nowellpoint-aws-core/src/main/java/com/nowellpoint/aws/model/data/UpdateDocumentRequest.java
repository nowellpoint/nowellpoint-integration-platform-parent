package com.nowellpoint.aws.model.data;

public class UpdateDocumentRequest extends AbstractDocumentRequest {

	private static final long serialVersionUID = 6391624974618347458L;
	
	public UpdateDocumentRequest() {
		
	}
	
	public String getCollectionName() {
		return super.getCollectionName();
	}

	public void setCollectionName(String collectionName) {
		super.setCollectionName(collectionName);
	}
	
	public String getDocument() {
		return super.getDocument();
	}

	public void setDocument(String document) {
		super.setDocument(document);
	}
	
	public UpdateDocumentRequest withAccountId(String accountId) {
		setAccountId(accountId);
		return this;
	}

	public UpdateDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
	
	public UpdateDocumentRequest withDocument(String document) {
		setDocument(document);
		return this;
	}
	
	public UpdateDocumentRequest withMongoDBConnectUri(String mongoDBConnectUri) {
		setMongoDBConnectUri(mongoDBConnectUri);
		return this;
	}
	
	public UpdateDocumentRequest withApiKey(String apiKey) {
		setApiKey(apiKey);
		return this;
	}
}