package com.nowellpoint.aws.data;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.service.DocumentService;

public class TestDataService {

	@Test
	public void testCreateRegistration() {
		
		long start = System.currentTimeMillis();
		
		ObjectNode json = JsonNodeFactory.instance.objectNode()
				.put("sicCode", "300")
				.put("hqBranchInd", "yes")
				.put("partyName", "Red Hat")
				.put("partyNumber", "228919")
				.put("version", 0);
				
		DocumentService documentService = new DocumentService();
		
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
		
		CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest().withCollectionName("registration").withDocument(json.toString());
		
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
		
		try {
			CreateDocumentResponse response = documentService.create(createDocumentRequest);
			
			System.out.println("id: " + response.getId());
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}