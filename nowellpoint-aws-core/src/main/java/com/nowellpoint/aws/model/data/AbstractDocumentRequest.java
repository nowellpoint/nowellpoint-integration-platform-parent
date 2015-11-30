package com.nowellpoint.aws.model.data;

import com.amazonaws.util.Base64;
import com.nowellpoint.aws.model.AbstractLambdaRequest;

public abstract class AbstractDocumentRequest extends AbstractLambdaRequest {
	
	private static final long serialVersionUID = 7500788112443398317L;
	private UserContext userContext;
	private String id;
	private String collectionName;
	private String document;

	public AbstractDocumentRequest() {
		
	}
	
	public UserContext getUserContext() {
		return userContext;
	}

	public void setUserContext(UserContext userContext) {
		this.userContext = userContext;
	}

	protected String getId() {
		return id;
	}

	protected void setId(String id) {
		this.id = id;
	}
	
	protected String getCollectionName() {
		return collectionName;
	}

	protected void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	protected String getDocument() {
		return new String(Base64.decode(document));
	}

	protected void setDocument(String document) {
		this.document = Base64.encodeAsString(document.getBytes());
	}
}