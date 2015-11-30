package com.nowellpoint.aws.model.data;

public class UpdateDocumentRequest extends AbstractDocumentRequest {

	private static final long serialVersionUID = 6391624974618347458L;
	
	public UpdateDocumentRequest() {
		
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
	
	public String getDocument() {
		return super.getDocument();
	}

	public void setDocument(String document) {
		super.setDocument(document);
	}
	
	public UpdateDocumentRequest withUserContext(UserContext userContext) {
		setUserContext(userContext);
		return this;
	}
	
	public UpdateDocumentRequest withId(String id) {
		setId(id);
		return this;
	}

	public UpdateDocumentRequest withCollectionName(String collectionName) {
		setCollectionName(collectionName);
		return this;
	}
	
	public UpdateDocumentRequest withDocument(String document) {
		setDocument(document);
		return this;
	}
}