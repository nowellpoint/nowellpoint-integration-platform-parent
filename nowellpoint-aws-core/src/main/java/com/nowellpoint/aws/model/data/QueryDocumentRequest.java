package com.nowellpoint.aws.model.data;

public class QueryDocumentRequest extends AbstractDocumentRequest {

	private static final long serialVersionUID = 6391624974618347458L;
	
	private String query;
	
	public QueryDocumentRequest() {
		
	}
	
	public String getCollectionName() {
		return super.getCollectionName();
	}
	
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
		
	public QueryDocumentRequest withMongoDBConnectUri(String mongoDBConnectUri) {
		setMongoDBConnectUri(mongoDBConnectUri);
		return this;
	}

	public QueryDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
	
	public QueryDocumentRequest withQuery(String query) {
		setQuery(query);
		return this;
	}
}