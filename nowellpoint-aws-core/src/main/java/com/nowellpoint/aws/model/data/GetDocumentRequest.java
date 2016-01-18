package com.nowellpoint.aws.model.data;

public class GetDocumentRequest extends AbstractDocumentRequest {

	private static final long serialVersionUID = -8063355241890824157L;
	
	private String id;
	
	public GetDocumentRequest() {
		
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
	
	public GetDocumentRequest withMongoDBConnectUri(String mongoDBConnectUri) {
		setMongoDBConnectUri(mongoDBConnectUri);
		return this;
	}
	
	public GetDocumentRequest withApiKey(String apiKey) {
		setApiKey(apiKey);
		return this;
	}
	
	public GetDocumentRequest withId(String id) {
		setId(id);
		return this;
	}

	public GetDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
	
	public GetDocumentRequest withAccountId(String accountId) {
		setAccountId(accountId);
		return this;
	}
}