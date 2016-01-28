package com.nowellpoint.aws.model.data;

public class QueryDocumentResponse extends AbstractDocumentResponse {

	private static final long serialVersionUID = -6801344091393248864L;
	
	private Integer count;
	
	private String queryResults;
	
	public QueryDocumentResponse() {
		
	}

	public String getQueryResults() {
		return queryResults;
	}

	public void setQueryResults(String queryResults) {
		this.queryResults = queryResults;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public void setCount(Integer count) {
		this.count = count;
	}
}