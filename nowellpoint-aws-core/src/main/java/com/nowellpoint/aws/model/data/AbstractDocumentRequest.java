package com.nowellpoint.aws.model.data;

import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public abstract class AbstractDocumentRequest extends AbstractLambdaRequest {
	
	private static final long serialVersionUID = 7500788112443398317L;
	private String mongoDBConnectUri;
	private String apiKey;
	private String accountId;
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
	
	@NotEmpty
	public String getApiKey() {
		return apiKey;
	}
	
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	@NotEmpty
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
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