package com.nowellpoint.aws.model.data;

public class QueryDocumentResponse extends AbstractDocumentResponse {

	private static final long serialVersionUID = -6801344091393248864L;
	
	private Integer count;
	
	public QueryDocumentResponse() {
		
	}

	public String getDocument() {
		return super.getDocument();
	}

	public void setDocument(String document) {
		super.setDocument(document);
	}
	
	public Integer getCount() {
		return count;
	}
	
	public void setCount(Integer count) {
		this.count = count;
	}
}