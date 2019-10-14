package com.nowellpoint.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.model.CreateResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.listener.model.TopicConfiguration;
import com.nowellpoint.util.SecretsManager;
import com.nowellpoint.util.SecureValue;
import com.nowellpoint.util.SecureValueException;

public class TestStreamingEventListener {
	
	private static final String ORGANIZATION_ID = "5bac3c0e0626b951816064f5";
	private static final String BUCKET = "streaming-event-listener-us-east-1-600862814314";
	private static final String KEY = "configuration/5bac3c0e0626b951816064f5";
	private static ObjectMapper mapper = new ObjectMapper();
	private static MongoClientURI mongoClientUri;
	private static MongoClient mongoClient;
	
	@BeforeClass
	public static void start() {
		mongoClientUri = new MongoClientURI(String.format("mongodb://%s", SecretsManager.getMongoClientUri()));
		mongoClient = new MongoClient(mongoClientUri);
	}
	
	//@Test
	public void testJsonB() {
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		S3ObjectIdBuilder builder = new S3ObjectIdBuilder()
				.withBucket(BUCKET)
				.withKey(KEY);
		
		S3Object object = s3client.getObject(new GetObjectRequest(builder.build()));	
		
		TopicConfiguration configuration = null;
		
		JsonbConfig config = new JsonbConfig().withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {

			@Override
			public boolean isVisible(Field field) {
				return true;
			}

			@Override
			public boolean isVisible(Method method) {
				return false;
			}
			
		});
		
		configuration = JsonbBuilder.create(config).fromJson(object.getObjectContent(), TopicConfiguration.class);
		
		assertNotNull(configuration.getOrganizationId());
		assertNotNull(configuration.getTopics().get(0).getChannel());
	}

	//@Test
	public void testTopicConfigurationChange() throws JsonParseException, JsonMappingException, IOException {
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		S3ObjectIdBuilder builder = new S3ObjectIdBuilder()
				.withBucket(BUCKET)
				.withKey(KEY);
		
		S3Object object = s3client.getObject(new GetObjectRequest(builder.build()));	
		
		JsonNode node = new ObjectMapper().readValue(object.getObjectContent(), JsonNode.class);
		
		byte[] bytes = node.toString().getBytes(StandardCharsets.UTF_8);
		InputStream input = new ByteArrayInputStream(bytes);
		
		ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        metadata.setContentLength(bytes.length);
		
		PutObjectRequest request = new PutObjectRequest(BUCKET, KEY, input, metadata);
        
        s3client.putObject(request);
	}
	
	@Test
	public void testListQueues() {
		
		final String S3_BUCKET = "streaming-event-listener-us-east-1-600862814314";
		
		FindIterable<Document> query = mongoClient.getDatabase(mongoClientUri.getDatabase())
				.getCollection("organizations")
				.find(new Document().append("_id", new ObjectId(ORGANIZATION_ID)));
		
		Document organization = query.first();
		
		List<Document> eventStreamListeners = organization.getList("eventStreamListeners", Document.class);
		
		JsonArrayBuilder builder = Json.createArrayBuilder();
		
		eventStreamListeners.forEach(l -> {
			builder.add(Json.createObjectBuilder()
					.add("channel", "/topic/" + l.getString("name"))
					.add("active", l.getBoolean("active"))
					.add("source", l.getString("source"))
					.add("topicId", l.getString("topicId") != null ? Json.createValue(l.getString("topicId")) : JsonValue.NULL)
					.build());
		});
		
		JsonObject json = Json.createObjectBuilder()
			     .add("organizationId", organization.get("_id").toString())
			     .add("apiVersion", Salesforce.API_VERSION)
			     .add("refreshToken", organization.get("connection", Document.class).getString("refreshToken"))
			     .add("changeEventsQueueUrl", "https://sqs.us-east-1.amazonaws.com/600862814314/d-change-event-queue.fifo")
			     .add("notificationsQueueUrl", "https://sqs.us-east-1.amazonaws.com/600862814314/notifications-sandbox")
			     .add("topics", builder.build())
			     .build();
		
		byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
		
		InputStream input = new ByteArrayInputStream(bytes);
		
		ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        metadata.setContentLength(bytes.length);
        
        String folder = "configuration/"
				.concat("streaming-event-listener-configuration-events-sandbox")
				.concat("/")
				.concat(organization.get("_id").toString());
		
		PutObjectRequest request = new PutObjectRequest(S3_BUCKET, folder, input, metadata);
		
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
        s3client.putObject(request);
	}
	
	@Test
	@Ignore
	public void testStreamingEventListener() {
		
		FindIterable<Document> query = mongoClient.getDatabase(mongoClientUri.getDatabase())
				.getCollection("organizations")
				.find(new Document().append("_id", new ObjectId(ORGANIZATION_ID)));
		
		Document organization = query.first();
		
		assertNotNull(organization.getString("name"));
		assertNotNull(organization.getString("number"));
		
		Document connection = organization.get("connection", Document.class);
		
		String refreshToken = null;
		
		try {
			refreshToken = SecureValue.decryptBase64(connection.getString("refreshToken"));
		} catch (SecureValueException e) {
			e.printStackTrace();
		}
		
		assertNotNull(connection.getString("refreshToken"));
		
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
				.setRefreshToken(refreshToken)
				.build();
		
		OauthAuthenticationResponse oauthAthenticationResponse = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		Token token = oauthAthenticationResponse.getToken();
		
		Salesforce client = SalesforceClientBuilder.defaultClient(token);
		
		Identity identity = client.getIdentity();
		
		int i = 0;
		
		while (i < 1) {
			CreateResult createResult = createAccount(token.getAccessToken(), identity.getUrls().getSObjects());
			updateAccount(token.getAccessToken(), identity.getUrls().getSObjects(), createResult.getId());
			deleteAccount(token.getAccessToken(), identity.getUrls().getSObjects(), createResult.getId());
			//updateAccount(token.getAccessToken(), identity.getUrls().getSObjects(), "0013000001Fc0b0AAB");
			//updateOpportunity(token.getAccessToken(), identity.getUrls().getSObjects(), "00630000002XCF9AAO");
			i++;
		}
	}
	
	@AfterClass
	public static void stop() {
		mongoClient.close();
	}
	
	private void updateAccount(String accessToken, String sobjectUrl, String accountId) {
		
		String body = mapper.createObjectNode()
				.put("Rating", "Hot")
				.put("Website", "https://www.nowellpoint.com") //www.archon-tech.com
				.toString();
		
		HttpResponse response = RestResource.post(sobjectUrl.concat("Account/").concat(accountId).concat("/?_HttpMethod=PATCH"))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(accessToken)
				.body(body)
				.contentType(MediaType.APPLICATION_JSON)
                .execute();
		
		assertEquals(204, response.getStatusCode());
	}
	
	private CreateResult createAccount(String accessToken, String sobjectUrl) {
		
		String body = mapper.createObjectNode()
				.put("BillingStreet", "129 S. Bloodworth Street")
				.put("BillingCity", "Raleigh")
				.put("BillingStateCode", "NC")
				.put("BillingPostalCode", "27601")
				.put("BillingCountryCode", "US")
				.put("Name", "Nowellpoint")
				.put("Rating", "Cold")
				.put("Website", "https://www.newsite.com")
				.toString();
		
		HttpResponse response = RestResource.post(sobjectUrl.concat("Account/"))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(accessToken)
				.body(body)
				.contentType(MediaType.APPLICATION_JSON)
                .execute();
		
		assertEquals(201, response.getStatusCode());
		
		return response.getEntity(CreateResult.class);
	}
	
	private void deleteAccount(String accessToken, String sobjectUrl, String accountId) {
		HttpResponse response = RestResource.delete(sobjectUrl.concat("Account/"))
				.path(accountId)
				.acceptCharset(StandardCharsets.UTF_8)
				.bearerAuthorization(accessToken)
                .execute();
		
		assertEquals(204, response.getStatusCode());
	}
	
	private void updateOpportunity(String accessToken, String sobjectUrl, String opportunityId) {
		
		String body = mapper.createObjectNode()
				.put("NextStep", "Call")
				.toString();
		
		HttpResponse response = RestResource.post(sobjectUrl.concat("Opportunity/").concat(opportunityId).concat("/?_HttpMethod=PATCH"))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(accessToken)
				.body(body)
				.contentType(MediaType.APPLICATION_JSON)
                .execute();
		
		assertEquals(204, response.getStatusCode());
	}
}