package com.nowellpoint.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.listener.model.S3Event;
import com.nowellpoint.listener.model.TopicConfiguration;
import com.nowellpoint.util.SecretsManager;

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
	public void testReadEvent() {
		String json = "{\"Records\":[{\"eventVersion\":\"2.1\",\"eventSource\":\"aws:s3\",\"awsRegion\":\"us-east-1\",\"eventTime\":\"2019-01-27T18:52:10.540Z\",\"eventName\":\"ObjectCreated:Put\",\"userIdentity\":{\"principalId\":\"AWS:AIDAJYIK27YZYWJELKV54\"},\"requestParameters\":{\"sourceIPAddress\":\"75.177.182.85\"},\"responseElements\":{\"x-amz-request-id\":\"95A634A5D6103E7D\",\"x-amz-id-2\":\"VaB4XmbH4hxyQ5IQnZ5T2SHaOndhwv4zpjHo3dyhkcULQgwNrusxLRLRJzOLmib+RtcT9McF89I=\"},\"s3\":{\"s3SchemaVersion\":\"1.0\",\"configurationId\":\"PutSQSNotification\",\"bucket\":{\"name\":\"streaming-event-listener-us-east-1-600862814314\",\"ownerIdentity\":{\"principalId\":\"A3O0Z0GOU0V258\"},\"arn\":\"arn:aws:s3:::streaming-event-listener-us-east-1-600862814314\"},\"object\":{\"key\":\"configuration/5bac3c0e0626b951816064f5\",\"size\":669,\"eTag\":\"6ab8b93abd9a9d3cc72e441e1b8ec711\",\"sequencer\":\"005C4DFDDA6E2DAABF\"}}}]}";
		
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
		
		S3Event event = JsonbBuilder.create(config).fromJson(json, S3Event.class);
		
		System.out.println(event.getRecords().get(0).getAwsRegion());
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
	public void testStreamingEventListener() {
		
		FindIterable<Document> query = mongoClient.getDatabase(mongoClientUri.getDatabase())
				.getCollection("organizations")
				.find(new Document().append("_id", new ObjectId(ORGANIZATION_ID)));
		
		Document organization = query.first();
		
		assertNotNull(organization.getString("name"));
		assertNotNull(organization.getString("number"));
		
		Document connection = organization.get("connection", Document.class);
		
		assertNotNull(connection.getString("refreshToken"));
		
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
				.setRefreshToken(connection.getString("refreshToken"))
				.build();
		
		OauthAuthenticationResponse oauthAthenticationResponse = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		Token token = oauthAthenticationResponse.getToken();
		
		Salesforce client = SalesforceClientBuilder.defaultClient(token);
		
		Identity identity = client.getIdentity();
		
		int i = 0;
		
		while (i < 5) {
			updateAccount(token.getAccessToken(), identity.getUrls().getSObjects(), "0013A00001YjszLQAR");
			updateOpportunity(token.getAccessToken(), identity.getUrls().getSObjects(), "00630000002XCF9AAO");
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