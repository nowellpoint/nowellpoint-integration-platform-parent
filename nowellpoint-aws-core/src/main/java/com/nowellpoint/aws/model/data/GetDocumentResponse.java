package com.nowellpoint.aws.model.data;

import org.bson.types.ObjectId;

public class GetDocumentResponse extends AbstractDocumentResponse {

	private static final long serialVersionUID = 6117232788854302357L;
	
	public GetDocumentResponse() {
		
	}

	public ObjectId getId() {
		return super.getId();
	}

	public void setId(ObjectId id) {
		super.setId(id);
	}

	public String getDocument() {
		return super.getDocument();
	}

	public void setDocument(String document) {
		super.setDocument(document);
	}
}