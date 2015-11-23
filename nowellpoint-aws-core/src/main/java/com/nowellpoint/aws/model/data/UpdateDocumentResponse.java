package com.nowellpoint.aws.model.data;

import com.amazonaws.util.Base64;
import com.nowellpoint.aws.model.AbstractResponse;

public class UpdateDocumentResponse extends AbstractResponse {

	private static final long serialVersionUID = -6801344091393248864L;
	
	private String id;
	
	private String document;
	
	public UpdateDocumentResponse() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDocument() {
		return new String(Base64.decode(document));
	}

	public void setDocument(String document) {
		this.document = Base64.encodeAsString(document.getBytes());
	}
}