package com.nowellpoint.aws.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.client.DataClient;
import com.nowellpoint.aws.idp.client.IdentityProviderClient;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.admin.PropertyStore;
import com.nowellpoint.aws.model.data.CreateDocumentRequest;
import com.nowellpoint.aws.model.data.CreateDocumentResponse;
import com.nowellpoint.aws.model.data.DeleteDocumentRequest;
import com.nowellpoint.aws.model.data.DeleteDocumentResponse;
import com.nowellpoint.aws.model.data.GetDocumentRequest;
import com.nowellpoint.aws.model.data.GetDocumentResponse;
import com.nowellpoint.aws.model.data.Identity;
import com.nowellpoint.aws.model.data.UpdateDocumentRequest;
import com.nowellpoint.aws.model.data.UpdateDocumentResponse;
import com.nowellpoint.aws.idp.model.GetTokenRequest;
import com.nowellpoint.aws.idp.model.GetTokenResponse;
import com.nowellpoint.aws.idp.model.RevokeTokenRequest;
import com.nowellpoint.aws.idp.model.RevokeTokenResponse;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.aws.tools.TokenParser;

public class TestDataClient {
	
	private static IdentityProviderClient identityProviderClient = new IdentityProviderClient();
	private static DataClient dataClient = new DataClient();
	private static Token token;
	
	private static ObjectNode json = JsonNodeFactory.instance.objectNode()
			.put("sicCode", "300")
			.put("hqBranchInd", "yes")
			.put("partyName", "Red Hat")
			.put("partyNumber", "228919")
			.put("version", 0);
	
	@BeforeClass
	public static void init() {
		
		Properties.setSystemProperties(PropertyStore.SANDBOX);
		
		long startTime = System.currentTimeMillis();
		
		System.out.println("Authenticating...");
		
		GetTokenRequest tokenRequest = new GetTokenRequest()
				.withApiEndpoint(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.withApplicationId(System.getProperty(Properties.STORMPATH_APPLICATION_ID))
				.withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withUsername(System.getenv("STORMPATH_USERNAME"))
				.withPassword(System.getenv("STORMPATH_PASSWORD"));
		
		GetTokenResponse tokenResponse = identityProviderClient.token(tokenRequest);
		
		assertNotNull(tokenResponse.getToken());
			
		System.out.println("Authenticating...success: " + tokenResponse.getToken().getStormpathAccessTokenHref());
		
		token = tokenResponse.getToken();
		
		String accountId = TokenParser.parseToken(System.getenv("STORMPATH_API_KEY_SECRET"), tokenResponse.getToken().getAccessToken()).getBody().getSubject();
		
		assertNotNull(accountId);
		
		System.out.println("Account: " + accountId);
		
		System.out.println("Authenticate: " + (System.currentTimeMillis() - startTime));
	}
	
	@Test
	public void testCreateAndUpdateIdentity() {
		
		System.out.println("testCreateAndUpdateIdentity");
		
		Identity identity = new Identity();
		identity.setCreatedDate(Date.from(Instant.now()));
		identity.setLastModifiedDate(Date.from(Instant.now()));
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String json = objectMapper.writeValueAsString(identity);
			System.out.println(json);
			identity = objectMapper.readValue(json, Identity.class);
			System.out.println(identity.getCreatedDate());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateAndUpdateParty() {
		
		System.out.println("testCreateAndUpdateParty");
		
		String accountId = TokenParser.parseToken(System.getenv("STORMPATH_API_KEY_SECRET"), token.getAccessToken()).getBody().getSubject();
		
		json.put("accountId", accountId);
		
		long startTime = System.currentTimeMillis();
		
		CreateDocumentRequest createDocumentRequest = new CreateDocumentRequest()
				.withMongoDBConnectUri(System.getProperty(Properties.MONGO_CLIENT_URI))
				.withCollectionName("parties")
				.withDocument(json.toString());
			
		CreateDocumentResponse createDocumentResponse = dataClient.create(createDocumentRequest);	
			
		assertTrue(createDocumentResponse.getStatusCode() == 201);
		assertNotNull(createDocumentResponse.getId());
			
		System.out.println("CreateDocumentResponse - execution time: " + String.valueOf(System.currentTimeMillis() - startTime));
		System.out.println("id: " + createDocumentResponse.getId());
						
		json.put("_id", createDocumentResponse.getId());
		json.put("partyType", "ORGANIZATION");
			
		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest()
				.withMongoDBConnectUri(System.getProperty(Properties.MONGO_CLIENT_URI))
				.withCollectionName("parties")
				.withDocument(json.toString());
			
		startTime = System.currentTimeMillis();
			
		UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
			
		assertTrue(updateDocumentResponse.getStatusCode() == 200);
		assertNotNull(updateDocumentResponse.getId());
			
		System.out.println("UpdateDocumentResponse - execution time: " + String.valueOf(System.currentTimeMillis() - startTime));			
		System.out.println("Id: " + updateDocumentResponse.getId());
			
		startTime = System.currentTimeMillis();
			
		GetDocumentRequest getDocumentRequest = new GetDocumentRequest()
				.withMongoDBConnectUri(System.getProperty(Properties.MONGO_CLIENT_URI))
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
			
		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest()
				.withMongoDBConnectUri(System.getProperty(Properties.MONGO_CLIENT_URI))
				.withCollectionName("parties")
				.withId(createDocumentResponse.getId());
			
		DeleteDocumentResponse deleteDocumentResponse = dataClient.delete(deleteDocumentRequest);
			
		System.out.println("DeleteDocumentResponse - execution time: " + String.valueOf(System.currentTimeMillis() - startTime));		
			
		assertTrue(deleteDocumentResponse.getStatusCode() == 204);
			
		assertNull(deleteDocumentResponse.getErrorCode());
		assertNull(deleteDocumentResponse.getErrorMessage());
	}
	
	@Test
	public void testNotFound() {
		
		String accountId = TokenParser.parseToken(System.getenv("STORMPATH_API_KEY_SECRET"), token.getAccessToken()).getBody().getSubject();
		
		json.put("accountId", accountId);
			
		GetDocumentRequest getDocumentRequest = new GetDocumentRequest()
				.withMongoDBConnectUri(System.getProperty(Properties.MONGO_CLIENT_URI))
				.withCollectionName("parties")
				.withId(UUID.randomUUID().toString());
			
		long startTime = System.currentTimeMillis();
			
		GetDocumentResponse getDocumentResponse = dataClient.get(getDocumentRequest);
			
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));	
			
		assertEquals(Integer.valueOf(getDocumentResponse.getStatusCode()), Integer.valueOf(404));
		assertEquals(getDocumentResponse.getErrorCode(), "not_found");
		assertNotNull(getDocumentResponse.getErrorMessage());
		assertNull(getDocumentResponse.getDocument());
		assertNull(getDocumentResponse.getId());
			
		System.out.println(getDocumentResponse.getErrorCode());
		
		json.put("_id", UUID.randomUUID().toString());
			
		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest()
				.withMongoDBConnectUri(System.getProperty(Properties.MONGO_CLIENT_URI))
				.withCollectionName("parties")
				.withDocument(json.toString());
			
		startTime = System.currentTimeMillis();
		
		System.out.println(System.getProperty(Properties.DEFAULT_ACCOUNT_ID));
			
		UpdateDocumentResponse updateDocumentResponse = dataClient.update(updateDocumentRequest);
		
		System.out.println(updateDocumentResponse.getErrorMessage());
			
		System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - startTime));	
			
		assertEquals(Integer.valueOf(updateDocumentResponse.getStatusCode()), Integer.valueOf(404));
		assertEquals(updateDocumentResponse.getErrorCode(), "not_found");
		assertNotNull(updateDocumentResponse.getErrorMessage());
		assertNull(getDocumentResponse.getDocument());
		assertNull(getDocumentResponse.getId());
			
		System.out.println(updateDocumentResponse.getErrorCode());
			
		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest().withMongoDBConnectUri(System.getProperty(Properties.MONGO_CLIENT_URI))
				.withMongoDBConnectUri(System.getProperty(Properties.MONGO_CLIENT_URI))
				.withCollectionName("parties")
				.withId(UUID.randomUUID().toString());
			
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
		RevokeTokenRequest revokeTokenRequest = new RevokeTokenRequest().withApiEndpoint(System.getProperty(Properties.STORMPATH_API_ENDPOINT))
				.withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withAccessToken(token.getAccessToken());
		
		RevokeTokenResponse revokeTokenResponse = identityProviderClient.token(revokeTokenRequest);
		
		assertTrue(revokeTokenResponse.getStatusCode() == 204);
	}
}