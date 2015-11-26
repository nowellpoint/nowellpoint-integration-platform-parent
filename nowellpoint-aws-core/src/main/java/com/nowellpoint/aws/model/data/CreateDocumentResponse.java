package com.nowellpoint.aws.model.data;

import com.nowellpoint.aws.model.AbstractResponse;

public class CreateDocumentResponse extends AbstractResponse {

	private static final long serialVersionUID = -6801344091393248864L;
	
	private String id;
	
	public CreateDocumentResponse() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}