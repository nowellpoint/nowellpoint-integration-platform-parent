package com.nowellpoint.aws.model.data;

public class GetDocumentRequest extends AbstractDocumentRequest {

	private static final long serialVersionUID = -8063355241890824157L;
	
	public GetDocumentRequest() {
		
	}

	public String getId() {
		return super.getId();
	}

	public void setId(String id) {
		super.setId(id);
	}
	
	public String getCollectionName() {
		return super.getCollectionName();
	}

	public void setCollectionName(String collectionName) {
		super.setCollectionName(collectionName);
	}
	
	public GetDocumentRequest withId(String id) {
		setId(id);
		return this;
	}

	public GetDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
}