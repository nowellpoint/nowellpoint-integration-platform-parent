package com.nowellpoint.listener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.listener.model.Payload;
import com.nowellpoint.listener.model.ReplayId;
import com.nowellpoint.listener.model.StreamingEvent;
import com.nowellpoint.listener.model.StreamingEventListenerConfiguration;
import com.nowellpoint.util.SecretsManager;

public class TopicSubscription {
	
	private static final Logger logger = Logger.getLogger(StreamingEventListener.class);
	private static ObjectMapper mapper = new ObjectMapper();
	
	private static final int CONNECTION_TIMEOUT = 20 * 1000;
    private static final int STOP_TIMEOUT = 120 * 1000; 
    private static final String REPLAY = "replay";

	private Datastore datastore;
	private HttpClient httpClient;
	private BayeuxClient client;
	private StreamingEventListenerConfiguration configuration;

	public TopicSubscription(Datastore datastore, StreamingEventListenerConfiguration configuration) {
		this.datastore = datastore;
		this.configuration = configuration;
		this.connect();
		this.subscribe();
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
	
	private Long getReplayId(String topicId) {
		ReplayId replayId = datastore.get(ReplayId.class, topicId);
		if (replayId != null) {
			return replayId.getReplayId();
		} else {
			return Long.valueOf(-2);
		}
	}
	
	private void disconnect() {
		client.disconnect();
		try {
			httpClient.stop();
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	private void subscribe() {
		client.getChannel(configuration.getChannel()).subscribe(new ClientSessionChannel.MessageListener() {

			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				
				Long start = System.currentTimeMillis();
				
				com.nowellpoint.client.sforce.model.StreamingEvent source = null;
	
				try {
					logger.info("**** start message received ****");
					JsonNode node = mapper.valueToTree(message.getDataAsMap());
					logger.info(node.toString());
					source = mapper.readValue(node.toString(), com.nowellpoint.client.sforce.model.StreamingEvent.class);
				} catch (Exception e) {
					logger.error(e);
				}
				
				Payload payload = new Payload();
				payload.setId(source.getSobject().getId());
				payload.setName(source.getSobject().getName());
				payload.setCreatedById(source.getSobject().getCreatedById());
				payload.setCreatedDate(source.getSobject().getCreatedDate());
				payload.setLastModifiedById(source.getSobject().getLastModifiedById());
				payload.setLastModifiedDate(source.getSobject().getLastModifiedDate());
				
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
				
				ReplayId streamingEventReplayId = new ReplayId();
				streamingEventReplayId.setId(configuration.getTopicId());
				streamingEventReplayId.setChannel(channel.getChannelId().getId());
				streamingEventReplayId.setReplayId(source.getEvent().getReplayId());
				
				datastore.save(streamingEventReplayId);
				
				logger.info("Execution Time: ".concat(String.valueOf(System.currentTimeMillis() - start)));
				logger.info("**** end message received ****");
			}
		});
		
		logger.info("Subscribed to channel: " + configuration.getChannel());
	}
	
	private void connect() {
		
		httpClient = new HttpClient();
		httpClient.setConnectTimeout(CONNECTION_TIMEOUT);
		httpClient.setStopTimeout(STOP_TIMEOUT);
		
        try {
			httpClient.start();
		} catch (Exception e) {
			logger.error(e);
			return;
		}
		
		Token token = refreshToken(configuration.getRefreshToken());
		
		LongPollingTransport httpTransport = new LongPollingTransport(null, httpClient) {
            @Override
            protected void customize(Request request) {
                request.header("Authorization", "OAuth " + token.getAccessToken());
            }
        };
        
        Long replayId = getReplayId(configuration.getTopicId());
        
        ConcurrentMap<String, Long> dataMap = new ConcurrentHashMap<>();
        dataMap.put(configuration.getChannel(), replayId);

        client = new BayeuxClient(token.getInstanceUrl().concat("/cometd/").concat(configuration.getApiVersion()), httpTransport);
        
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
				if (! message.isSuccessful()) {
					JsonNode node = mapper.valueToTree(message);
					logger.error(node.asText());
					disconnect();
					connect();
				}
			}
		});
        
        client.getChannel(Channel.META_CONNECT).addListener(new ClientSessionChannel.MessageListener() {
			
			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				if (! message.isSuccessful()) {
					JsonNode node = mapper.valueToTree(message);
					logger.error(node.asText());
					disconnect();
					connect();
				}
				
			}
		});
        
        client.getChannel(Channel.META_SUBSCRIBE).addListener(new ClientSessionChannel.MessageListener() {
			
			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				if (! message.isSuccessful()) {
					logger.error(message.get("error"));
				}
			}
		});
        
        client.handshake();
        
        boolean connected = client.waitFor(TimeUnit.SECONDS.toMillis(60), BayeuxClient.State.CONNECTED);
        
        if (!connected) {
        	logger.error("unable to connect");
        }
	}
}