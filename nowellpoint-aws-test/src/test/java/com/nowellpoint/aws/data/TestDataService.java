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
import com.nowellpoint.aws.model.idp.GetTokenRequest;
import com.nowellpoint.aws.model.idp.GetTokenResponse;
import com.nowellpoint.aws.service.DocumentService;
import com.nowellpoint.aws.service.IdentityProviderService;

public class TestDataService {

	@Test
	public void testCreateAndUpdateParty() {
		
//		IdentityProviderService identityProviderService = new IdentityProviderService();
//		
//		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("STORMPATH_USERNAME"))
//				.withPassword(System.getenv("STORMPATH_PASSWORD"));
//		
//		GetTokenResponse tokenResponse = null;
//		try {
//			tokenResponse = identityProviderService.authenticate(tokenRequest);
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		long startTime = System.currentTimeMillis();
		
		DocumentService documentService = new DocumentService();
		
		ObjectNode json = JsonNodeFactory.instance.objectNode()
				.put("sicCode", "300")
				.put("hqBranchInd", "yes")
				.put("partyName", "Red Hat")
				.put("partyNumber", "228919")
				.put("version", 0);
		
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));
		
		CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest().withCollectionName("parties").withDocument(json.toString());
		
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));
		
		try {
			CreateDocumentResponse createDocumentResponse = documentService.create(createDocumentRequest);
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));
			
			System.out.println("id: " + createDocumentResponse.getId());
			System.out.println("document: " + createDocumentResponse.getDocument());
			
			json.put("partyType", "ORGANIZATION");
			
			startTime = System.currentTimeMillis();
			
			UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest().withCollectionName("parties")
					.withId(createDocumentResponse.getId())
					.withDocument(json.toString());
			
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