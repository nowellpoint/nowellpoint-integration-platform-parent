package com.nowellpoint.aws.model.data;

import org.bson.types.ObjectId;

public class UpdateDocumentResponse extends AbstractDocumentResponse {

	private static final long serialVersionUID = -6801344091393248864L;
	
	public UpdateDocumentResponse() {
		
	}

	public ObjectId getId() {
		return super.getId();
	}

	public void setId(ObjectId id) {
		super.setId(id);
	}
}