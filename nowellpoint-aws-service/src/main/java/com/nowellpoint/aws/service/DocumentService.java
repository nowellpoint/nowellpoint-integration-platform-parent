package com.nowellpoint.aws.service;

import java.io.IOException;

import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;

public class DocumentService extends AbstractService {

	public CreateDocumentResponse create(CreateDocumentRequest documentRequest) throws IOException {
		return invoke("IDP_UsernamePasswordAuthentication", documentRequest, CreateDocumentResponse.class);
	}
}
