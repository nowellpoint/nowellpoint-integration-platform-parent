package com.nowellpoint.aws.model.data;

import org.hibernate.validator.constraints.NotEmpty;

public class CreateDocumentRequest extends AbstractDocumentRequest {

	private static final long serialVersionUID = 6391624974618347458L;
	
	public CreateDocumentRequest() {
		
	}
	
	@NotEmpty
	public String getCollectionName() {
		return super.getCollectionName();
	}
	
	@NotEmpty
	public String getDocument() {
		return super.getDocument();
	}

	public CreateDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
	
	public CreateDocumentRequest withDocument(String document) {
		setDocument(document);
		return this;
	}
	
	public CreateDocumentRequest withMongoDBConnectUri(String mongoDBConnectUri) {
		setMongoDBConnectUri(mongoDBConnectUri);
		return this;
	}
}