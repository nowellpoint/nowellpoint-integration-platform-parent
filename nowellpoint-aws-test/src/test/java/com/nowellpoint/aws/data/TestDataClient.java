package com.nowellpoint.aws.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.client.DataClient;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.GetDocumentRequest;
import com.nowellpoint.aws.model.data.GetDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;

public class TestDataClient {
	
	private static DataClient client = new DataClient();

	@Test
	public void testCreateAndUpdateParty() {
		
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
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));
			System.out.println("id: " + createDocumentResponse.getId());
						
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
			
			startTime = System.currentTimeMillis();
			
			GetDocumentRequest getDocumentRequest = new GetDocumentRequest().withCollection("parties")
					.withId(createDocumentResponse.getId());
			
			GetDocumentResponse getDocumentResponse = client.get(getDocumentRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));		
			
			assertEquals(Integer.valueOf(getDocumentResponse.getStatusCode()), Integer.valueOf(200));
			assertNotNull(getDocumentResponse.getId());
			assertNotNull(getDocumentResponse.getDocument());
			assertNull(getDocumentResponse.getErrorCode());
			assertNull(getDocumentResponse.getErrorMessage());
			
			System.out.println(getDocumentResponse.getDocument());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testNotFound() {
		
		try {
			
			GetDocumentRequest getDocumentRequest = new GetDocumentRequest().withCollection("parties")
					.withId("5656fc2ad53d130001a15bc6");
			
			long startTime = System.currentTimeMillis();
			
			GetDocumentResponse getDocumentResponse = client.get(getDocumentRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));	
			
			assertEquals(Integer.valueOf(getDocumentResponse.getStatusCode()), Integer.valueOf(404));
			assertEquals(getDocumentResponse.getErrorCode(), "not_found");
			assertNotNull(getDocumentResponse.getErrorMessage());
			assertNull(getDocumentResponse.getDocument());
			assertNull(getDocumentResponse.getId());
			
			System.out.println(getDocumentResponse.getErrorCode());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}