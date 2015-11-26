package com.nowellpoint.aws.model.data;

import com.nowellpoint.aws.model.AbstractRequest;

public class GetDocumentRequest extends AbstractRequest {

	private static final long serialVersionUID = -8063355241890824157L;
	
	private String id;
	private String collectionName;
	
	public GetDocumentRequest() {
		
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
	
	public GetDocumentRequest withId(String id) {
		setId(id);
		return this;
	}

	public GetDocumentRequest withCollection(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
}