package com.nowellpoint.aws.model.data;

public class CreateDocumentRequest extends DocumentRequest {

	private static final long serialVersionUID = 6391624974618347458L;
	
	public CreateDocumentRequest() {
		
	}

	public CreateDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
	
	public CreateDocumentRequest withDocument(String document) {
		setDocument(document);
		return this;
	}
}