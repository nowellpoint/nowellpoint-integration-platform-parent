package com.nowellpoint.console.sforce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.client.sforce.CreateResult;
import com.nowellpoint.client.sforce.model.ApexClass;
import com.nowellpoint.client.sforce.model.ApexTrigger;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.UserLicense;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalResult;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.EnvironmentVariables;
import com.nowellpoint.console.util.SecretsManager;
import com.nowellpoint.http.HttpRequestException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;

public class TestSalesforceServices {

	private static final Logger logger = Logger.getLogger(TestSalesforceServices.class.getName());
	private static ObjectMapper mapper = new ObjectMapper();
	
	private static final int CONNECTION_TIMEOUT = 20 * 1000;
    private static final int STOP_TIMEOUT = 120 * 1000; 
    private static final String REPLAY = "replay";
	
	private AtomicLong replayId = new AtomicLong();
	private ConcurrentMap<String, Long> dataMap = new ConcurrentHashMap<>();
	
	@BeforeClass
	public static void start() {
		
	}
	
	@Test
	public void testDashboardComponents() {
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get("5bac3c0e0626b951816064f5");
		
		assertNotNull(organization.getName());
		assertNotNull(organization.getNumber());
		
		logger.info(organization.getConnection().getRefreshToken());
		
		Token token = ServiceClient.getInstance()
				.salesforce()
				.refreshToken(organization.getConnection().getRefreshToken());
		
		DescribeGlobalResult describeGlobalResult = ServiceClient.getInstance()
				.salesforce()
				.describeGlobal(token);
		
		AtomicInteger customObjectCount = new AtomicInteger(0);
		
		describeGlobalResult.getSobjects().stream().forEach(sobject -> {
			if (sobject.getCustom()) {
				customObjectCount.getAndIncrement();
			}
		});
		
		logger.info(customObjectCount);
		
		Set<UserLicense> licenses = ServiceClient.getInstance()
				.salesforce()
				.getUserLicenses(token);
		
		logger.info(licenses.size());
		
		Set<ApexClass> classes = ServiceClient.getInstance()
				.salesforce()
				.getApexClasses(token);
		
		logger.info(classes.size());
		
		Set<ApexTrigger> triggers = ServiceClient.getInstance()
				.salesforce()
				.getApexTriggers(token);
		
		logger.info(triggers.size());
		
	}
	
	//@Test
	public void testCountTrends() throws HttpRequestException, IOException {
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get("5bac3c0e0626b951816064f5");
		
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
			BayeuxClient client = createClient(token.getInstanceUrl(), token.getAccessToken(), "AccountUpdateTopic");
			updateAccount(token.getAccessToken(), sobjectUrl, "0013A00001YjszLQAR");
			TimeUnit.SECONDS.sleep(10);
			client.disconnect();
			logger.info("**** replay id ****");
			createClient(token.getInstanceUrl(), token.getAccessToken(), "AccountUpdateTopic");
			updateAccount(token.getAccessToken(), sobjectUrl, "0013A00001YjszLQAR");
			TimeUnit.SECONDS.sleep(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		deleteTopic(token.getAccessToken(), sobjectUrl, topicId);
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
		
		String query = "SELECT Id, CreatedDate, LastModifiedDate FROM Account";
		
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
	
	private BayeuxClient createClient(String instanceUrl, String accessToken, String channel) throws Exception {
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
					@SuppressWarnings("unchecked")
        			public void onMessage(ClientSessionChannel channel, Message message) {
        				logger.info("**** message received ****");
        				logger.info(message.isSuccessful());
        				logger.info(message.getClientId());
        				logger.info(channel.getChannelId());
        				Map<String,Object> data = message.getDataAsMap();
        				replayId.set((Long)((Map<String, Object>)data.get("event")).get("replayId"));
        				logger.info("**** start message data ****");
        				logger.info(data);
        				logger.info("**** end message data ****");
        			}
        		});
        	}
        });
        
        return client;
    }
	
	@AfterClass
	public static void stop() {
		
	}
	
	private Long getReplayId(Message.Mutable message) {
		return replayId.get();
	}
}