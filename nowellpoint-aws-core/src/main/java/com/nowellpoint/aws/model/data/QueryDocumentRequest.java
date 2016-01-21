package com.nowellpoint.aws.model.data;

public class QueryDocumentRequest extends AbstractDocumentRequest {

	private static final long serialVersionUID = 6391624974618347458L;
	
	public QueryDocumentRequest() {
		
	}
	
	public String getCollectionName() {
		return super.getCollectionName();
	}
	
	public String getDocument() {
		return super.getDocument();
	}
		
	public QueryDocumentRequest withMongoDBConnectUri(String mongoDBConnectUri) {
		setMongoDBConnectUri(mongoDBConnectUri);
		return this;
	}

	public QueryDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
	
	public QueryDocumentRequest withDocument(String document) {
		setDocument(document);
		return this;
	}
}