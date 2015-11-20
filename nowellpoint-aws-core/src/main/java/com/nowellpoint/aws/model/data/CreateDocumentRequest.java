package com.nowellpoint.aws.model.data;

import com.amazonaws.util.Base64;
import com.nowellpoint.aws.model.AbstractRequest;

public class CreateDocumentRequest extends AbstractRequest {

	private static final long serialVersionUID = 6391624974618347458L;
	
	private String collectionName;
	
	private String document;
	
	public CreateDocumentRequest() {
		
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	public String getDocument() {
		return new String(Base64.decode(document));
	}

	public void setDocument(String document) {
		this.document = Base64.encodeAsString(document.getBytes());
	}

	public CreateDocumentRequest withCollectionName(String collectionName) {
		this.collectionName = collectionName;
		return this;
	}
	
	public CreateDocumentRequest withDocument(String document) {
		setDocument(document);
		return this;
	}
}