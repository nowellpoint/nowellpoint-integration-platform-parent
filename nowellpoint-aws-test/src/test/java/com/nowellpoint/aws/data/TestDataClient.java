package com.nowellpoint.aws.data;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.client.DataClient;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;

public class TestDataClient {

	@Test
	public void testCreateAndUpdateParty() {
		
		DataClient client = new DataClient();
		
		ObjectNode json = JsonNodeFactory.instance.objectNode()
				.put("sicCode", "300")
				.put("hqBranchInd", "yes")
				.put("partyName", "Red Hat")
				.put("partyNumber", "228919")
				.put("version", 0);
		
		try {
			
			CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest().withCollectionName("parties")
					.withDocument(json.toString());
			
			long startTime = System.currentTimeMillis();
			
			CreateDocumentResponse createDocumentResponse = client.create(createDocumentRequest);	
			
			assertTrue(createDocumentResponse.getStatusCode() == 201);
			assertNotNull(createDocumentResponse.getId());
			assertNotNull(createDocumentResponse.getDocument());
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));
			System.out.println("id: " + createDocumentResponse.getId());
			System.out.println("document: " + createDocumentResponse.getDocument());
						
			json.put("partyType", "ORGANIZATION");
			
			UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest().withCollectionName("parties")
					.withId(createDocumentResponse.getId())
					.withDocument(json.toString());
			
			startTime = System.currentTimeMillis();
			
			UpdateDocumentResponse updateDocumentResponse = client.update(updateDocumentRequest);
			
			assertTrue(updateDocumentResponse.getStatusCode() == 200);
			assertNotNull(updateDocumentRequest.getId());
			assertNotNull(updateDocumentRequest.getDocument());
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));			
			System.out.println("id: " + updateDocumentResponse.getId());
			System.out.println("document: " + updateDocumentResponse.getDocument());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}