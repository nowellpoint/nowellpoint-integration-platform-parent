package com.nowellpoint.aws.model.data;

import java.util.Base64;
import java.util.Optional;

import com.nowellpoint.aws.model.AbstractResponse;

public class GetDocumentResponse extends AbstractResponse {

	private static final long serialVersionUID = 6117232788854302357L;

	private String id;
	
	private String document;
	
	public GetDocumentResponse() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDocument() {
		if (Optional.ofNullable(document).isPresent()) {
			return new String(Base64.getDecoder().decode(document));
		} else {
			return null;
		}
	}

	public void setDocument(String document) {
		this.document = Base64.getEncoder().encodeToString(document.getBytes());
	}
}