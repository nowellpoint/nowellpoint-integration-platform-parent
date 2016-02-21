package com.nowellpoint.aws.model.data;

import org.bson.types.ObjectId;

public class CreateDocumentResponse extends AbstractDocumentResponse {

	private static final long serialVersionUID = -6801344091393248864L;
	
	public CreateDocumentResponse() {
		
	}

	public ObjectId getId() {
		return super.getId();
	}

	public void setId(ObjectId id) {
		super.setId(id);
	}
}