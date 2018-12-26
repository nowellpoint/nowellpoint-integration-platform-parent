package com.nowellpoint.console.mongodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

import com.nowellpoint.client.sforce.Authenticators;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.PushTopicRequest;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.model.CreateResult;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.console.entity.AggregationResult;
import com.nowellpoint.console.entity.StreamingEvent;
import com.nowellpoint.console.entity.StreamingEventDAO;
import com.nowellpoint.console.entity.StreamingEventListener;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.entity.Organization;
import com.nowellpoint.console.entity.OrganizationDAO;
import com.nowellpoint.console.entity.Payload;
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
	
	private Map<String,StreamingEventListener> eventListenerMap = new HashMap<String,StreamingEventListener>();
	
	@BeforeClass
	public static void start() {
		MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", SecretsManager.getMongoClientUri()));
		mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
	}
	
	@Test
	public void testSalesforceIdentity() {

		Organization organization = datastore.get(Organization.class, new ObjectId("5bac3c0e0626b951816064f5"));
		
		assertNotNull(organization.getName());
		assertNotNull(organization.getNumber());
		
		System.out.println(organization.getConnection().getRefreshToken());
		
		Token token = ServiceClient.getInstance()
				.salesforce()
				.refreshToken(organization.getConnection().getRefreshToken());
		
		long start = System.currentTimeMillis();
		
		ServiceClient.getInstance().salesforce().getIdentity(token);
		
		logger.info("getIdentity execution time: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		
		Identity identity = ServiceClient.getInstance()
				.salesforce()
				.getIdentity(token);
		
		assertTrue((System.currentTimeMillis() - start) < 3);
		assertNotNull(identity);
		assertNotNull(identity.getActive());
		assertNotNull(identity.getCity());
		assertNotNull(identity.getCountry());
		assertNotNull(identity.getState());
		assertNotNull(identity.getStreet());
		assertNotNull(identity.getPostalCode());
		assertNotNull(identity.getAssertedUser());
		assertNotNull(identity.getDisplayName());
		assertNotNull(identity.getEmail());
		assertNotNull(identity.getFirstName());
		assertNotNull(identity.getLastName());
		assertNotNull(identity.getId());
		assertNotNull(identity.getLanguage());
		assertNotNull(identity.getLocale());
		
		logger.info(identity.getDisplayName());
	}
	
	@Test
	@Ignore
	public void testGroupBy() throws IOException {
		
		OrganizationDAO dao = new OrganizationDAO(Organization.class, datastore);
		
		List<AggregationResult> results = dao.getEventsLastDays(new ObjectId("5bac3c0e0626b951816064f5"), 7);
		
		String data = results.stream()
				.sorted(Comparator.reverseOrder())
				.map(r -> formatLabel(Locale.getDefault(), r))
				.collect(Collectors.joining(", "));
		
		System.out.println(data);
	}
	
	@Test
	@Ignore
	public void testCountTrends() throws HttpRequestException, IOException {
		
		Organization organization = datastore.get(Organization.class, new ObjectId("5bac3c0e0626b951816064f5"));
		
		assertNotNull(organization.getName());
		assertNotNull(organization.getNumber());
		
		System.out.println(organization.getConnection().getRefreshToken());
		
		eventListenerMap = organization.getStreamingEventListeners()
				.stream()
				.collect(Collectors.toMap(el -> el.getPrefix(), el -> el));
		
		Salesforce client = SalesforceClientBuilder.builder().build().getClient();
		
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
				.setRefreshToken(organization.getConnection().getRefreshToken())
				.build();
		
		OauthAuthenticationResponse oauthAthenticationResponse = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		Token token = oauthAthenticationResponse.getToken();
		
		Long start = System.currentTimeMillis();
		
		Identity identity = client.getIdentity(token);
		
		logger.info("getIdentity execution time: " + (System.currentTimeMillis() - start));
		
		HttpResponse queryResponse = RestResource.get(identity.getUrls().getQuery())
				.acceptCharset(StandardCharsets.UTF_8)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.queryParameter("q", "Select Id,Name,CreatedDate,LastModifiedDate From Account")
                .execute();
		
		JsonNode response = mapper.readTree(queryResponse.getAsString());
		
		logger.info(response.get("totalSize").asInt());
		
		String sobjectUrl = identity.getUrls().getSObjects();
		
		String topicId = createAccountTopic(token);
		
		try {
			BayeuxClient bayeuxClient = createClient(organization, token.getInstanceUrl(), token.getAccessToken(), "AccountUpdateTopic");
			updateAccount(token.getAccessToken(), sobjectUrl, "0013A00001YjszLQAR");
			TimeUnit.SECONDS.sleep(10);
			bayeuxClient.disconnect();
			logger.info("**** replay id ****");
			createClient(organization, token.getInstanceUrl(), token.getAccessToken(), "AccountUpdateTopic");
			updateAccount(token.getAccessToken(), sobjectUrl, "0013A00001YjszLQAR");
			TimeUnit.SECONDS.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		client.deletePushTopic(token, topicId);
		
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
	
	private String createAccountTopic(Token token) throws HttpRequestException, IOException {
		
		String query = "SELECT Id, Name, CreatedById, CreatedDate, LastModifiedById, LastModifiedDate FROM Account";
		
		PushTopicRequest request = PushTopicRequest.builder()
				.name("AccountUpdateTopic")
				.query(query)
				.apiVersion("44.0")
				.notifyForOperationCreate(Boolean.TRUE)
				.notifyForOperationDelete(Boolean.TRUE)
				.notifyForOperationUndelete(Boolean.TRUE)
				.notifyForOperationUpdate(Boolean.TRUE)
				.notifyForFields("All")
				.build();
		
		Salesforce client = SalesforceClientBuilder.builder().build().getClient();
		
		CreateResult createResult = client.createPushTopic(token, request);
		
		assertEquals(Boolean.TRUE, createResult.getSuccess());
		assertNotNull(createResult.getId());
		
		return createResult.getId();
	}
	
	private BayeuxClient createClient(Organization organization, String instanceUrl, String accessToken, String channel) throws Exception {
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
        				
        				com.nowellpoint.client.sforce.model.StreamingEvent source = null;
        	
        				try {
        					logger.info("**** start message data ****");
        					JsonNode node = mapper.valueToTree(message.getDataAsMap());
        					logger.info(node.toString());
        					logger.info("**** end message data ****");
        					source = mapper.readValue(node.toString(), com.nowellpoint.client.sforce.model.StreamingEvent.class);
        				} catch (Exception e) {
        					e.printStackTrace();
        				}
        				
        				replayId.set(source.getEvent().getReplayId());
        				
        				StreamingEventDAO dao = new StreamingEventDAO(StreamingEvent.class, datastore);
        				
        				Payload payload = new Payload();
        				payload.setId(source.getSObject().getId());
        				payload.setName(source.getSObject().getName());
        				payload.setCreatedById(source.getSObject().getCreatedById());
        				payload.setCreatedDate(source.getSObject().getCreatedDate());
        				payload.setLastModifiedById(source.getSObject().getLastModifiedById());
        				payload.setLastModifiedDate(source.getSObject().getLastModifiedDate());
        				
        				StreamingEvent streamingEvent = new StreamingEvent();
        				streamingEvent.setEventDate(source.getEvent().getCreatedDate());
        				streamingEvent.setOrganizationId(organization.getId());
        				streamingEvent.setReplayId(source.getEvent().getReplayId());
        				streamingEvent.setType(source.getEvent().getType());
        				streamingEvent.setSource(eventListenerMap.get(source.getSObject().getId().substring(0, 3)).getSource());
        				streamingEvent.setPayload(payload);
        				
        				dao.save(streamingEvent);
        				
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
	
	private static String formatLabel(Locale locale, AggregationResult result) {
		
		ZoneId utc = ZoneId.of( "UTC" );
		
		LocalDate now = LocalDate.now( utc ).minusDays(Integer.valueOf(result.getId()));
		
		String text = null;
		if (now.equals(LocalDate.now( utc ))) {
			text = "Today";
		} else if (now.equals(LocalDate.now( utc ).minusDays(1))) {
			text = "Yesterday";
		} else {
			text = now.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
		}
		
		return new StringBuilder("['")
				.append(text)
				.append("'")
				.append(", ")
				.append(result.getCount())
				.append("]")
				.toString();
	}
}