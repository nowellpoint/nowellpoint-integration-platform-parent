package com.nowellpoint.console.mongodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.bson.types.ObjectId;
import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.jboss.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.client.sforce.CreateResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.console.entity.AggregationResult;
import com.nowellpoint.console.entity.Event;
import com.nowellpoint.console.entity.EventDAO;
import com.nowellpoint.console.entity.Organization;
import com.nowellpoint.console.entity.OrganizationDAO;
import com.nowellpoint.console.util.EnvironmentVariables;
import com.nowellpoint.console.util.SecretsManager;
import com.nowellpoint.http.HttpRequestException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;

public class TestGroupBy {
	
	private static final Logger logger = Logger.getLogger(TestGroupBy.class.getName());
	private static ObjectMapper mapper = new ObjectMapper();
	
	private static MongoClient mongoClient;
	private static Datastore datastore;
	
	private static final int CONNECTION_TIMEOUT = 20 * 1000;
    private static final int STOP_TIMEOUT = 120 * 1000; 
    private static final String REPLAY = "replay";
	
	private AtomicLong replayId = new AtomicLong();
	private ConcurrentMap<String, Long> dataMap = new ConcurrentHashMap<>();
	
	@BeforeClass
	public static void start() {
		MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", SecretsManager.getMongoClientUri()));
		mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
	}
	
	@Test
	@Ignore
	public void testGroupBy() throws IOException {
		
		OrganizationDAO dao = new OrganizationDAO(Organization.class, datastore);
		
		List<AggregationResult> results = dao.getEventsLastDays(new ObjectId("5bac3c0e0626b951816064f5"), 7);
		
		results.stream().forEach(e -> {
			System.out.println(e.getId() + " : " + e.getCount());
		});
	}
	
	@Test
	//@Ignore
	public void testCountTrends() throws HttpRequestException, IOException {
		
		Organization organization = datastore.get(Organization.class, new ObjectId("5bac3c0e0626b951816064f5"));
		
		assertNotNull(organization.getName());
		assertNotNull(organization.getNumber());
		
		System.out.println(organization.getConnection().getRefreshToken());
		
		HttpResponse tokenResponse = RestResource.get(EnvironmentVariables.getSalesforceTokenUri())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
                .queryParameter("grant_type", "refresh_token")
                .queryParameter("refresh_token", organization.getConnection().getRefreshToken())
                .queryParameter("client_id", SecretsManager.getSalesforceClientId())
                .queryParameter("client_secret", SecretsManager.getSalesforceClientSecret())
                .execute();
		
		Token token = tokenResponse.getEntity(Token.class);
		
		HttpResponse identityResponse = RestResource.get(token.getId())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.queryParameter("version", "latest")
				.execute();
		
		Identity identity = identityResponse.getEntity(Identity.class);
		
		HttpResponse queryResponse = RestResource.get(identity.getUrls().getQuery())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.queryParameter("q", "Select Id,Name,CreatedDate,LastModifiedDate From Account")
                .execute();
		
		JsonNode response = mapper.readTree(queryResponse.getAsString());
		
		logger.info(response.get("totalSize").asInt());
		
		String sobjectUrl = identity.getUrls().getSobjects();
		
		String topicId = createAccountTopic(token.getAccessToken(), sobjectUrl);
		
		try {
			BayeuxClient client = createClient(token.getInstanceUrl(), token.getAccessToken(), "AccountUpdateTopic", organization.getId());
			updateAccount(token.getAccessToken(), sobjectUrl, "0013A00001YjszLQAR");
			TimeUnit.SECONDS.sleep(10);
			client.disconnect();
			logger.info("**** replay id ****");
			createClient(token.getInstanceUrl(), token.getAccessToken(), "AccountUpdateTopic", organization.getId());
			updateAccount(token.getAccessToken(), sobjectUrl, "0013A00001YjszLQAR");
			TimeUnit.SECONDS.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		deleteTopic(token.getAccessToken(), sobjectUrl, topicId);
		
		testGroupBy();
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
	
	private String createAccountTopic(String accessToken, String sobjectUrl) throws HttpRequestException, IOException {
		
		String query = "SELECT Id, CreatedById, CreatedDate, LastModifiedById, LastModifiedDate FROM Account";
		
		String body = mapper.createObjectNode()
				.put("Name", "AccountUpdateTopic")
				.put("Query", query)
				.put("ApiVersion", "44.0")
				.put("NotifyForOperationCreate", Boolean.TRUE)
				.put("NotifyForOperationUpdate", Boolean.TRUE)
				.put("NotifyForOperationUndelete", Boolean.TRUE)
				.put("NotifyForOperationDelete", Boolean.TRUE)
				.put("NotifyForFields", "All")
				.toString();
		
		HttpResponse response = RestResource.post(sobjectUrl.concat("PushTopic/"))
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(accessToken)
				.body(body)
				.contentType(MediaType.APPLICATION_JSON)
                .execute();
		
		assertEquals(201, response.getStatusCode());
		
		CreateResult createResult = response.getEntity(CreateResult.class);
		
		assertEquals(Boolean.TRUE, createResult.getSuccess());
		assertNotNull(createResult.getId());
		
		return createResult.getId();
	}
	
	private void deleteTopic(String accessToken, String sobjectUrl, String topicId) {
		
		HttpResponse response = RestResource.delete(sobjectUrl.concat("PushTopic/").concat(topicId))
				.bearerAuthorization(accessToken)
				.execute();
		
		assertEquals(204, response.getStatusCode());
	}
	
	private BayeuxClient createClient(String instanceUrl, String accessToken, String channel, ObjectId organizationId) throws Exception {
		HttpClient httpClient = new HttpClient();
		httpClient.setConnectTimeout(CONNECTION_TIMEOUT);
		httpClient.setStopTimeout(STOP_TIMEOUT);
        httpClient.start();
        
        LongPollingTransport httpTransport = new LongPollingTransport(null, httpClient) {
            @Override
            protected void customize(Request request) {
                request.header("Authorization", "OAuth " + accessToken);
            }
        };

        BayeuxClient client = new BayeuxClient(instanceUrl.concat("/cometd/44.0"), httpTransport);
        
        client.addExtension(new ClientSession.Extension() {
        	
        	@Override
            public boolean rcv(ClientSession session, Message.Mutable message) {
                Long replayId = getReplayId(message);
                if (replayId != null) {
                    try {
                        dataMap.put(message.getChannel(), replayId);
                    } catch (ClassCastException e) {
                        return false;
                    }
                }
                return true;
            }
        	
        	@Override
            public boolean sendMeta(ClientSession session, Message.Mutable message) {
                switch (message.getChannel()) {
                case Channel.META_HANDSHAKE:
                    message.getExt(true).put(REPLAY, Boolean.TRUE);
                    break;
                case Channel.META_SUBSCRIBE:
                	if (!dataMap.isEmpty()) {
                		message.getExt(true).put(REPLAY, dataMap);
                	}
                    break;
                }
                return true;
            }
        });
        
        client.getChannel(Channel.META_HANDSHAKE).addListener(new ClientSessionChannel.MessageListener() {
			
			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				System.out.println("**** start handshake ****");
				System.out.println(message.isSuccessful());
				System.out.println(message.getClientId());
				if (! message.isSuccessful()) {
					System.out.println(message.get("error"));
					System.out.println(message.get("failure"));
				}
			}
		});
        client.getChannel(Channel.META_CONNECT).addListener(new ClientSessionChannel.MessageListener() {
			
			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				System.out.println("**** connect ****");
				System.out.println(message.isSuccessful());
				System.out.println(message.getClientId());
				System.out.println(channel.getChannelId());
				if (! message.isSuccessful()) {
					System.out.println(message.get("error"));
					System.out.println(message.get("failure"));
				}
				
			}
		});
        client.getChannel(Channel.META_SUBSCRIBE).addListener(new ClientSessionChannel.MessageListener() {
			
			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				System.out.println("**** subscribe ****");
				System.out.println(message.isSuccessful());
				System.out.println(message.getClientId());
				System.out.println(channel.getChannelId());
				if (! message.isSuccessful()) {
					System.out.println(message.get("error"));
					System.out.println(message.get("failure"));
				}
				
			}
		});
        
        client.handshake();
        
        boolean connected = client.waitFor(TimeUnit.SECONDS.toMillis(60), BayeuxClient.State.CONNECTED);
        
        if (!connected) {
        	System.out.println("unable to connect");
        	System.exit(1);
        }
        
        client.batch(new Runnable() {
        	public void run() {
        		client.getChannel("/topic/".concat(channel)).subscribe(new ClientSessionChannel.MessageListener() {

					@Override
        			public void onMessage(ClientSessionChannel channel, Message message) {
						
						Long start = System.currentTimeMillis();
						
        				logger.info("**** message received ****");
        				logger.info(message.isSuccessful());
        				logger.info(message.getClientId());
        				logger.info(channel.getChannelId());
        				
        				JsonNode node = null;
        				try {
        					node = mapper.valueToTree(message.getDataAsMap());
        				} catch (Exception e) {
        					e.printStackTrace();
        				}
        				
        				replayId.set(node.get("event").get("replayId").asLong());
        				
        				logger.info("**** start message data ****");
        				logger.info(node.toString());
        				logger.info("**** end message data ****");
        				
        				Long replayId = node.get("event").get("replayId").asLong();
        				Date eventDate = Date.from( Instant.parse(node.get("event").get("createdDate").asText()) );
        				String type = node.get("event").get("type").asText();
        				
        				JsonNode sobject = node.get("sobject");
        				String salesforceId = sobject.get("Id").asText();
        				String createdById = sobject.get("CreatedById").asText();
        				Date createdDate = Date.from( Instant.parse(sobject.get("CreatedDate").asText()) );
        				String lastModifiedById = sobject.get("LastModifiedById").asText();
        				Date lastModifiedDate = Date.from( Instant.parse(sobject.get("LastModifiedDate").asText()) );
        				
        				EventDAO dao = new EventDAO(Event.class, datastore);
        				
        				Event event = new Event();
        				event.setCreatedById(createdById);
        				event.setCreatedDate(createdDate);
        				event.setEventDate(eventDate);
        				event.setLastModifiedById(lastModifiedById);
        				event.setLastModifiedDate(lastModifiedDate);
        				event.setOrganizationId(organizationId);
        				event.setReplayId(replayId);
        				event.setSalesforceId(salesforceId);
        				event.setType(type);
        				
        				dao.save(event);
        				
        				logger.info("Execution Time: ".concat(String.valueOf(System.currentTimeMillis() - start)));
        			}
        		});
        	}
        });
        
        return client;
    }
	
	@AfterClass
	public static void stop() {
		mongoClient.close();
	}
	
	private Long getReplayId(Message.Mutable message) {
		return replayId.get();
	}
}