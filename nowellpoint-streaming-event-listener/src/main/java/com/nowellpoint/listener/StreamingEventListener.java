package com.nowellpoint.listener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.jboss.logging.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.QueryResults;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.listener.model.Payload;
import com.nowellpoint.listener.model.StreamingEvent;
import com.nowellpoint.listener.model.StreamingEventListenerConfiguration;
import com.nowellpoint.listener.model.StreamingEventReplayId;
import com.nowellpoint.util.SecretsManager;

@WebListener
public class StreamingEventListener implements ServletContextListener {
	
	private static final Logger logger = Logger.getLogger(StreamingEventListener.class);
	private static ObjectMapper mapper = new ObjectMapper();
	
	private static final int CONNECTION_TIMEOUT = 20 * 1000;
    private static final int STOP_TIMEOUT = 120 * 1000; 
    private static final String REPLAY = "replay";
    
    private MongoClient mongoClient;
	private Datastore datastore;
    
    @Override
	public void contextInitialized(ServletContextEvent event) {
    	
    	MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", SecretsManager.getMongoClientUri()));
		mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();
        morphia.map(StreamingEvent.class);
        morphia.map(StreamingEventReplayId.class);
        morphia.map(StreamingEventListenerConfiguration.class);

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
        
        QueryResults<StreamingEventListenerConfiguration> queryResults = datastore.find(StreamingEventListenerConfiguration.class).filter("active =", Boolean.TRUE);
        
        queryResults.asList().stream().forEach(c -> {
        	try {
				createClient(c);
			} catch (Exception e) {
				e.printStackTrace();
			}
        });
    }
    
    @Override
	public void contextDestroyed(ServletContextEvent event) {
    	mongoClient.close();
    }
    
    private Token refreshToken(String refreshToken) {
		
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
				.setRefreshToken(refreshToken)
				.build();
		
		OauthAuthenticationResponse oauthAthenticationResponse = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		return oauthAthenticationResponse.getToken();
    }

	private BayeuxClient createClient(final StreamingEventListenerConfiguration configuration) throws Exception {
		
		Token token = refreshToken(configuration.getRefreshToken());
		
		HttpClient httpClient = new HttpClient();
		httpClient.setConnectTimeout(CONNECTION_TIMEOUT);
		httpClient.setStopTimeout(STOP_TIMEOUT);
        httpClient.start();
        
        LongPollingTransport httpTransport = new LongPollingTransport(null, httpClient) {
            @Override
            protected void customize(Request request) {
                request.header("Authorization", "OAuth " + token.getAccessToken());
            }
        };
        
        Long replayId = getReplayId(configuration.getTopicId());
        String channel = "/topic/".concat(configuration.getChannel());
        
        ConcurrentMap<String, Long> dataMap = new ConcurrentHashMap<>();
        dataMap.put(channel, replayId);

        BayeuxClient client = new BayeuxClient(token.getInstanceUrl().concat("/cometd/").concat(configuration.getApiVersion()), httpTransport);
        
        client.addExtension(new ClientSession.Extension() {
        	
        	@Override
            public boolean rcv(ClientSession session, Message.Mutable message) {
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
					logger.info(message.get("error"));
					logger.info(message.get("failure"));
				}
				
			}
		});
        
        client.handshake();
        
        boolean connected = client.waitFor(TimeUnit.SECONDS.toMillis(60), BayeuxClient.State.CONNECTED);
        
        if (!connected) {
        	logger.error("unable to connect");
        	System.exit(1);
        }
        
        client.batch(new Runnable() {
        	public void run() {
        		client.getChannel(channel).subscribe(new ClientSessionChannel.MessageListener() {

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
        					logger.error(e);
        				}
        				
        				Payload payload = new Payload();
        				payload.setId(source.getSObject().getId());
        				payload.setName(source.getSObject().getName());
        				payload.setCreatedById(source.getSObject().getCreatedById());
        				payload.setCreatedDate(source.getSObject().getCreatedDate());
        				payload.setLastModifiedById(source.getSObject().getLastModifiedById());
        				payload.setLastModifiedDate(source.getSObject().getLastModifiedDate());
        				
        				StreamingEvent streamingEvent = new StreamingEvent();
        				streamingEvent.setEventDate(source.getEvent().getCreatedDate());
        				streamingEvent.setOrganizationId(configuration.getOrganizationId());
        				streamingEvent.setReplayId(source.getEvent().getReplayId());
        				streamingEvent.setType(source.getEvent().getType());
        				streamingEvent.setSource(configuration.getSource());
        				streamingEvent.setPayload(payload);
        				
        				try {
        					datastore.save(streamingEvent);
        				} catch (com.mongodb.DuplicateKeyException e) {
        					logger.info(e.getErrorMessage());
        				}
        				
        				StreamingEventReplayId streamingEventReplayId = new StreamingEventReplayId();
        				streamingEventReplayId.setId(configuration.getTopicId());
        				streamingEventReplayId.setChannel(channel.getChannelId().getId());
        				streamingEventReplayId.setReplayId(source.getEvent().getReplayId());
        				
        				datastore.save(streamingEventReplayId);
        				
        				logger.info("Execution Time: ".concat(String.valueOf(System.currentTimeMillis() - start)));
        			}
        		});
        	}
        });
        
        return client;
    }
	
	private Long getReplayId(String topicId) {
		StreamingEventReplayId replayId = datastore.get(StreamingEventReplayId.class, topicId);
		if (replayId != null) {
			return replayId.getReplayId();
		} else {
			return Long.valueOf(-2);
		}
	}
}