package com.nowellpoint.aws.client;

import com.nowellpoint.aws.model.ClientException;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.DeleteDocumentRequest;
import com.nowellpoint.aws.model.data.DeleteDocumentResponse;
import com.nowellpoint.aws.model.data.GetDocumentRequest;
import com.nowellpoint.aws.model.data.GetDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;
import com.nowellpoint.aws.model.data.QueryDocumentRequest;
import com.nowellpoint.aws.model.data.QueryDocumentResponse;

public class DataClient extends AbstractClient {
	
	public DataClient() {
		
	}

	public CreateDocumentResponse create(CreateDocumentRequest createDocumentRequest) throws ClientException {
		return invoke("CreateDocument", createDocumentRequest, CreateDocumentResponse.class);
	}
	
	public UpdateDocumentResponse update(UpdateDocumentRequest updateDocumentRequest) throws ClientException {
		return invoke("UpdateDocument", updateDocumentRequest, UpdateDocumentResponse.class);
	}
	
	public GetDocumentResponse get(GetDocumentRequest getDocumentRequest) throws ClientException {
		return invoke("GetDocument", getDocumentRequest, GetDocumentResponse.class);
	}
	
	public DeleteDocumentResponse delete(DeleteDocumentRequest deleteDocumentRequest) throws ClientException {
		return invoke("DeleteDocument", deleteDocumentRequest, DeleteDocumentResponse.class);
	}
	
	public QueryDocumentResponse query(QueryDocumentRequest queryDocumentRequest) throws ClientException {
		return invoke("QueryDocument", queryDocumentRequest, QueryDocumentResponse.class);
	}
}