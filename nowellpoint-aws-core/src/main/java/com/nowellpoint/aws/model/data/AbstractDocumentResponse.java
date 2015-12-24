package com.nowellpoint.aws.model.data;

import java.util.Optional;

import com.nowellpoint.aws.model.AbstractLambdaResponse;

public abstract class AbstractDocumentResponse extends AbstractLambdaResponse {

	private static final long serialVersionUID = 906350726714634877L;
	
	private String id;
	
	private String document;

	public AbstractDocumentResponse() {
		
	}
	
	protected String getId() {
		return id;
	}
	
	protected void setId(String id) {
		this.id = id;
	}
	
	protected String getDocument() {
		if (Optional.ofNullable(document).isPresent()) {
			return decode(document);
		} else {
			return null;
		}
	}
	
	protected void setDocument(String document) {
		this.document = encode(document);
	}
}