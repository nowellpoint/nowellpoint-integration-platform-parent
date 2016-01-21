package com.nowellpoint.aws.model.data;

import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public abstract class AbstractDocumentRequest extends AbstractLambdaRequest {
	
	private static final long serialVersionUID = 7500788112443398317L;
	private String mongoDBConnectUri;
	private String collectionName;
	private String document;

	public AbstractDocumentRequest() {
		
	}
	
	@NotEmpty
	public String getMongoDBConnectUri() {
		return mongoDBConnectUri;
	}

	public void setMongoDBConnectUri(String mongoDBConnectUri) {
		this.mongoDBConnectUri = mongoDBConnectUri;
	}
	
	protected String getCollectionName() {
		return collectionName;
	}

	protected void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	protected String getDocument() {
		return decode(document);
	}

	protected void setDocument(String document) {
		this.document = encode(document);
	}
}