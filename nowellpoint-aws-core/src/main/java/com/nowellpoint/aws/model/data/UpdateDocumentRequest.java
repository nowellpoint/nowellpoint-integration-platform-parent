package com.nowellpoint.aws.model.data;

import com.amazonaws.util.Base64;
import com.nowellpoint.aws.model.AbstractRequest;

public class UpdateDocumentRequest extends AbstractRequest {

	private static final long serialVersionUID = 6391624974618347458L;
	
	private String id;
	
	private String collectionName;
	
	private String document;
	
	public UpdateDocumentRequest() {
		
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
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
	
	public UpdateDocumentRequest withId(String id) {
		setId(id);
		return this;
	}

	public UpdateDocumentRequest withCollectionName(String collectionName) {
		this.collectionName = collectionName;
		return this;
	}
	
	public UpdateDocumentRequest withDocument(String document) {
		setDocument(document);
		return this;
	}
}