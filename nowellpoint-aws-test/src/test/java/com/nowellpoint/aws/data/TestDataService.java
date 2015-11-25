package com.nowellpoint.aws.data;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;
import com.nowellpoint.aws.service.DocumentService;

public class TestDataService {

	@Test
	public void testCreateAndUpdateParty() {
		
		DocumentService documentService = new DocumentService();
		
		ObjectNode json = JsonNodeFactory.instance.objectNode()
				.put("sicCode", "300")
				.put("hqBranchInd", "yes")
				.put("partyName", "Red Hat")
				.put("partyNumber", "228919")
				.put("version", 0);
		
		CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest().withCollectionName("parties").withDocument(json.toString());
		
		try {
			long startTime = System.currentTimeMillis();
			CreateDocumentResponse createDocumentResponse = documentService.create(createDocumentRequest);	
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));
			System.out.println("id: " + createDocumentResponse.getId());
			System.out.println("document: " + createDocumentResponse.getDocument());
			
			json.put("partyType", "ORGANIZATION");
			
			UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest().withCollectionName("parties")
					.withId(createDocumentResponse.getId())
					.withDocument(json.toString());
			
			startTime = System.currentTimeMillis();
			UpdateDocumentResponse updateDocumentResponse = documentService.update(updateDocumentRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));			
			System.out.println("id: " + updateDocumentResponse.getId());
			System.out.println("document: " + updateDocumentResponse.getDocument());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}