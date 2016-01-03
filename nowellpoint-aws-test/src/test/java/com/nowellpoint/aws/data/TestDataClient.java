package com.nowellpoint.aws.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.client.DataClient;
import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.DeleteDocumentRequest;
import com.nowellpoint.aws.model.data.DeleteDocumentResponse;
import com.nowellpoint.aws.model.data.GetDocumentRequest;
import com.nowellpoint.aws.model.data.GetDocumentResponse;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;
import com.nowellpoint.aws.model.data.UserContext;
import com.nowellpoint.aws.model.idp.GetCustomDataRequest;
import com.nowellpoint.aws.model.idp.GetCustomDataResponse;
import com.nowellpoint.aws.model.idp.GetTokenRequest;
import com.nowellpoint.aws.model.idp.GetTokenResponse;

public class TestDataClient {
	
	private static IdentityProviderClient identityProviderClient = new IdentityProviderClient();
	private static DataClient dataClient = new DataClient();
	private static UserContext userContext;
	private static String accessToken;
	
	private static ObjectNode json = JsonNodeFactory.instance.objectNode()
			.put("sicCode", "300")
			.put("hqBranchInd", "yes")
			.put("partyName", "Red Hat")
			.put("partyNumber", "228919")
			.put("version", 0);
	
	@BeforeClass
	public static void init() {
		
		long startTime = System.currentTimeMillis();
		
		System.out.println("Authenticating...");
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("STORMPATH_USERNAME"))
				.withPassword(System.getenv("STORMPATH_PASSWORD"));
		
		GetTokenResponse tokenResponse = identityProviderClient.authenticate(tokenRequest);
			
		accessToken = tokenResponse.getToken().getAccessToken();
			
		System.out.println("Authenticating...success: " + tokenResponse.getToken().getStormpathAccessTokenHref());
		System.out.println("Setting up session...");
			
		GetCustomDataRequest customDataRequest = new GetCustomDataRequest().withAccessToken(accessToken);
			
		GetCustomDataResponse customDataResponse = identityProviderClient.customData(customDataRequest);
			
		userContext = new UserContext().withMongoDBConnectUri(customDataResponse.getCustomData().getMongodbConnectUri())
				.withUserId(customDataResponse.getCustomData().getApplicationUserId());
			
		System.out.println("Setting up session...complete");
		
		System.out.println("Authenticate: " + (System.currentTimeMillis() - startTime));
	}

	@Test
	public void testCreateAndUpdateParty() {
		
		long startTime = System.currentTimeMillis();
		
		CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest().withMongoDBConnectUri(userContext.getMongoDBConnectUri())
				.withUserId(userContext.getUserId())
				.withCollectionName("parties")
				.withDocument(json.toString());
			
		CreateDocumentResponse createDocumentResponse = dataClient.create(createDocumentRequest);	
			
		assertTrue(createDocumentResponse.getStatusCode() == 200);
		assertNotNull(createDocumentResponse.getId());
			
		System.out.println("CreateDocumentResponse - execution time: " + String.valueOf(System.currentTimeMillis() - startTime));
		System.out.println("id: " + createDocumentResponse.getId());
						
		json.put("partyType", "ORGANIZATION");
			
		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest().withMongoDBConnectUri(userContext.getMongoDBConnectUri())
				.withUserId(userContext.getUserId())
				.withCollectionName("parties")
				.withId(createDocumentResponse.getId())
				.withDocument(json.toString());
			
		startTime = System.currentTimeMillis();
			
		UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
			
		assertTrue(updateDocumentResponse.getStatusCode() == 200);
		assertNotNull(updateDocumentRequest.getId());
		assertNotNull(updateDocumentRequest.getDocument());
			
		System.out.println("UpdateDocumentResponse - execution time: " + String.valueOf(System.currentTimeMillis() - startTime));			
		System.out.println("id: " + updateDocumentResponse.getId());
			
		startTime = System.currentTimeMillis();
			
		GetDocumentRequest getDocumentRequest = new GetDocumentRequest().withMongoDBConnectUri(userContext.getMongoDBConnectUri())
				.withCollectionName("parties")
				.withId(createDocumentResponse.getId());
			
		GetDocumentResponse getDocumentResponse = dataClient.get(getDocumentRequest);
			
		System.out.println("GetDocumentResponse - execution time: " + String.valueOf(System.currentTimeMillis() - startTime));		
			
		assertEquals(Integer.valueOf(getDocumentResponse.getStatusCode()), Integer.valueOf(200));
		assertNotNull(getDocumentResponse.getId());
		assertNotNull(getDocumentResponse.getDocument());
		assertNull(getDocumentResponse.getErrorCode());
		assertNull(getDocumentResponse.getErrorMessage());
			
		System.out.println(getDocumentResponse.getDocument());
			
		startTime = System.currentTimeMillis();
			
		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest().withMongoDBConnectUri(userContext.getMongoDBConnectUri())
				.withCollectionName("parties")
				.withId(createDocumentResponse.getId());
			
		DeleteDocumentResponse deleteDocumentResponse = dataClient.delete(deleteDocumentRequest);
			
		System.out.println("DeleteDocumentResponse - execution time: " + String.valueOf(System.currentTimeMillis() - startTime));		
			
		assertEquals(Integer.valueOf(getDocumentResponse.getStatusCode()), Integer.valueOf(200));
			
		assertNull(deleteDocumentResponse.getErrorCode());
		assertNull(deleteDocumentResponse.getErrorMessage());
	}
	
	@Test
	public void testNotFound() {
			
		GetDocumentRequest getDocumentRequest = new GetDocumentRequest().withMongoDBConnectUri(userContext.getMongoDBConnectUri())
				.withCollectionName("parties")
				.withId("5656fc2ad53d130001a15bc6");
			
		long startTime = System.currentTimeMillis();
			
		GetDocumentResponse getDocumentResponse = dataClient.get(getDocumentRequest);
			
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));	
			
		assertEquals(Integer.valueOf(getDocumentResponse.getStatusCode()), Integer.valueOf(404));
		assertEquals(getDocumentResponse.getErrorCode(), "not_found");
		assertNotNull(getDocumentResponse.getErrorMessage());
		assertNull(getDocumentResponse.getDocument());
		assertNull(getDocumentResponse.getId());
			
		System.out.println(getDocumentResponse.getErrorCode());
			
		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest().withMongoDBConnectUri(userContext.getMongoDBConnectUri())
				.withUserId(userContext.getUserId())
				.withCollectionName("parties")
				.withId("5656fc2ad53d130001a15bc6")
				.withDocument(json.toString());
			
		startTime = System.currentTimeMillis();
			
		UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
			
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));	
			
		assertEquals(Integer.valueOf(updateDocumentResponse.getStatusCode()), Integer.valueOf(404));
		assertEquals(updateDocumentResponse.getErrorCode(), "not_found");
		assertNotNull(updateDocumentResponse.getErrorMessage());
		assertNull(getDocumentResponse.getDocument());
		assertNull(getDocumentResponse.getId());
			
		System.out.println(updateDocumentResponse.getErrorCode());
			
		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest().withMongoDBConnectUri(userContext.getMongoDBConnectUri())
				.withCollectionName("parties")
				.withId("5656fc2ad53d130001a15bc6");
			
		startTime = System.currentTimeMillis();
			
		DeleteDocumentResponse deleteDocumentResponse = dataClient.delete(deleteDocumentRequest);
			
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));	
			
		assertEquals(Integer.valueOf(deleteDocumentResponse.getStatusCode()), Integer.valueOf(404));
		assertEquals(deleteDocumentResponse.getErrorCode(), "not_found");
		assertNotNull(deleteDocumentResponse.getErrorMessage());
			
		System.out.println(deleteDocumentResponse.getErrorCode());
	}
	
	@AfterClass
	public static void cleanUp() {

	}
}