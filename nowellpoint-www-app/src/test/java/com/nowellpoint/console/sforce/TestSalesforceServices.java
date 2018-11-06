package com.nowellpoint.console.sforce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import org.jboss.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.console.entity.Organization;
import com.nowellpoint.console.entity.OrganizationDAO;
import com.nowellpoint.console.util.EnvironmentVariables;
import com.nowellpoint.http.HttpRequestException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;

public class TestSalesforceServices {

	private static final Logger logger = Logger.getLogger(TestSalesforceServices.class.getName());
	private static final int CONNECTION_TIMEOUT = 20 * 1000;  // milliseconds
    private static final int READ_TIMEOUT = 120 * 1000; // milliseconds
	private static ObjectMapper mapper = new ObjectMapper();
	private static Datastore datastore;
	
	@BeforeClass
	public static void start() {
		MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", EnvironmentVariables.getMongoClientUri()));
		MongoClient mongoClient = new MongoClient(mongoClientUri);
		Morphia morphia = new Morphia();
		
		datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
	}
	
	@Test
	public void testCountTrends() throws HttpRequestException, IOException {
		OrganizationDAO dao = new OrganizationDAO(com.nowellpoint.console.entity.Organization.class, datastore);
		Organization organization = dao.get(new ObjectId("5bac3c0e0626b951816064f5"));
		logger.info(organization.getName());
		logger.info(organization.getNumber());
		
		ObjectMapper mapper = new ObjectMapper();
		
		HttpResponse tokenResponse = RestResource.post(EnvironmentVariables.getSalesforceTokenUri())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .parameter("grant_type", "password")
                .parameter("username", System.getenv("SALESFORCE_USERNAME"))
                .parameter("password", System.getenv("SALESFORCE_PASSWORD").concat(System.getenv("SALESFORCE_SECURITY_TOKEN")))
                .parameter("client_id", EnvironmentVariables.getSalesforceClientId())
                .parameter("client_secret", EnvironmentVariables.getSalesforceClientSecret())
                .execute();
		
		JsonNode tokenNode = mapper.readTree(tokenResponse.getAsString());
		
		String id = tokenNode.get("id").asText();
		String accessToken = tokenNode.get("access_token").asText();
		
		HttpResponse identityResponse = RestResource.get(id)
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(accessToken)
				.queryParameter("version", "latest")
				.execute();
		
		JsonNode identityNode = mapper.readTree(identityResponse.getAsString());
		
		HttpResponse queryResponse = RestResource.get(identityNode.get("urls").get("query").asText())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(accessToken)
				.queryParameter("q", "Select Id,Name,CreatedDate,LastModifiedDate From Account")
                .execute();
		
		JsonNode response = mapper.readTree(queryResponse.getAsString());
		
		logger.info(response.get("totalSize").asInt());
		
		String topicId = createAccountTopic(accessToken, identityNode.get("urls").get("sobjects").asText());
		
		deleteTopic(accessToken, identityNode.get("urls").get("sobjects").asText(), topicId);
	}
	
	private String createAccountTopic(String accessToken, String sobjectUrl) throws HttpRequestException, IOException {
		
		String query = "SELECT Id, CreatedDate, LastModifiedDate FROM Account";
		
		String body = new ObjectMapper().createObjectNode()
				.put("Name", "AccountUpdateTopic")
				.put("Query", query)
				.put("ApiVersion", "44.0")
				.put("NotifyForOperationCreate", Boolean.TRUE)
				.put("NotifyForOperationUpdate", Boolean.TRUE)
				.put("NotifyForOperationUndelete", Boolean.TRUE)
				.put("NotifyForOperationDelete", Boolean.TRUE)
				.put("NotifyForFields", "Referenced")
				.toString();
		
		HttpResponse response = RestResource.post(sobjectUrl.concat("PushTopic/"))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(accessToken)
				.body(body)
				.contentType(MediaType.APPLICATION_JSON)
                .execute();
		
		assertEquals(201, response.getStatusCode());
		
		JsonNode responseNode = mapper.readTree(response.getAsString());
		assertEquals(Boolean.TRUE, responseNode.get("success").asBoolean());
		assertNotNull(responseNode.get("id"));
		return responseNode.get("id").asText();
	}
	
	private void deleteTopic(String accessToken, String sobjectUrl, String topicId) {
		
		HttpResponse response = RestResource.delete(sobjectUrl.concat("PushTopic/").concat(topicId))
				.bearerAuthorization(accessToken)
				.execute();
		
		assertEquals(204, response.getStatusCode());
	}
	
	private void connectToTopic(String accessToken) {
		
	}
	
//	private static BayeuxClient makeClient(String accessToken) throws Exception {
//        HttpClient httpClient = new HttpClient();
//        httpClient.setConnectTimeout(CONNECTION_TIMEOUT);
//        //httpClient.setTimeout(READ_TIMEOUT);
//        httpClient.start();
//
//        Map<String, Object> options = new HashMap<String, Object>();
//        //options.put(ClientTransport., READ_TIMEOUT);
//        LongPollingTransport transport = new LongPollingTransport(
//          options, httpClient) {
//
//        	@Override
//            protected void customize(ContentExchange exchange) {
//                super.customize(exchange);
//                exchange.addRequestHeader("Authorization", "OAuth " + accessToken);
//            }
//        };
//
//        BayeuxClient client = new BayeuxClient(salesforceStreamingEndpoint(
//            endpoint), transport);
//        
//        return client;
//    }
	
	@AfterClass
	public static void stop() {
		datastore.getMongo().close();
	}
}