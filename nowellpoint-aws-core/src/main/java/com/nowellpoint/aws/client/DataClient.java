package com.nowellpoint.aws.client;

import java.io.IOException;

import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.DeleteDocumentRequest;
import com.nowellpoint.aws.model.data.DeleteDocumentResponse;
import com.nowellpoint.aws.model.data.GetDocumentRequest;
import com.nowellpoint.aws.model.data.GetDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;

public class DataClient extends AbstractClient {
	
	private String accessToken;
	
	public DataClient() {
		
	}

	public CreateDocumentResponse create(CreateDocumentRequest createDocumentRequest) throws IOException {
		return invoke("CreateDocument", createDocumentRequest, CreateDocumentResponse.class, accessToken);
	}
	
	public UpdateDocumentResponse update(UpdateDocumentRequest updateDocumentRequest) throws IOException {
		return invoke("UpdateDocument", updateDocumentRequest, UpdateDocumentResponse.class);
	}
	
	public GetDocumentResponse get(GetDocumentRequest getDocumentRequest) throws IOException {
		return invoke("GetDocument", getDocumentRequest, GetDocumentResponse.class);
	}
	
	public DeleteDocumentResponse delete(DeleteDocumentRequest deleteDocumentRequest) throws IOException {
		return invoke("DeleteDocument", deleteDocumentRequest, DeleteDocumentResponse.class);
	}
}