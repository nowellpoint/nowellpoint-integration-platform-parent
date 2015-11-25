package com.nowellpoint.aws.model.data;

import com.amazonaws.util.Base64;
import com.nowellpoint.aws.model.AbstractRequest;

public abstract class DocumentRequest extends AbstractRequest {
	
	private static final long serialVersionUID = 7500788112443398317L;
	private String collectionName;
	private String document;

	public DocumentRequest() {
		
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
}