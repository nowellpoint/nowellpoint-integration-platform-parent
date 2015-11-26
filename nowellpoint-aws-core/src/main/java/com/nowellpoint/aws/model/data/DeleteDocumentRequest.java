package com.nowellpoint.aws.model.data;

import com.nowellpoint.aws.model.AbstractRequest;

public class DeleteDocumentRequest extends AbstractRequest {
	
	private static final long serialVersionUID = -6090910215991875135L;
	private String id;
	private String collectionName;
	
	public DeleteDocumentRequest() {
		
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
	
	public DeleteDocumentRequest withId(String id) {
		setId(id);
		return this;
	}

	public DeleteDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
}