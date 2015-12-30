package com.nowellpoint.aws.model.data;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public abstract class AbstractDocumentRequest extends AbstractLambdaRequest {
	
	private static final long serialVersionUID = 7500788112443398317L;
	private String mongoDBConnectUri;
	private String apiKey;
	private String userId;
	private String id;
	private String collectionName;
	private String document;

	public AbstractDocumentRequest() {
		
	}
	
	public String getMongoDBConnectUri() {
		return mongoDBConnectUri;
	}

	public void setMongoDBConnectUri(String mongoDBConnectUri) {
		this.mongoDBConnectUri = mongoDBConnectUri;
	}
	
	public String getApiKey() {
		return apiKey;
	}
	
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
		return decode(document);
	}

	protected void setDocument(String document) {
		this.document = encode(document);
	}
}