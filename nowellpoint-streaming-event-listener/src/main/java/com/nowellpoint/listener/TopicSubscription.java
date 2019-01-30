package com.nowellpoint.listener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;
import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.jboss.logging.Logger;

import com.mongodb.DuplicateKeyException;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.listener.model.TopicConfiguration;
import com.nowellpoint.listener.connection.MongoConnection;
import com.nowellpoint.listener.model.Payload;
import com.nowellpoint.listener.model.StreamingEvent;
import com.nowellpoint.util.SecretsManager;

import lombok.Builder;

public class TopicSubscription {
	
	private static final Logger LOGGER = Logger.getLogger(StreamingEventListener.class);
	private static final String REPLAY = "replay";
	private static final int CONNECTION_TIMEOUT = 20 * 1000;
    private static final int READ_TIMEOUT = 120 * 1000; 
    private static final int STOP_TIMEOUT = 120 * 1000;

    private TopicConfiguration configuration;
	private HttpClient httpClient;
	private BayeuxClient client;

	@Builder
	private TopicSubscription(TopicConfiguration configuration) {
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
	
	public void reconnect(TopicConfiguration configuration) {
		this.configuration = configuration;
		connect();
	}
	
	private void stopHttpClient() {
		try {
			httpClient.stop();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}
	
	public void disconnect() {
		configuration.getTopics().stream().forEach( t -> {
			client.getChannel(t.getChannel()).unsubscribe();
		});
		client.disconnect();
		client.waitFor(TimeUnit.SECONDS.toMillis(60), BayeuxClient.State.DISCONNECTED);
		stopHttpClient();
	}
	
	private void subscribe() {
		configuration.getTopics().stream().filter(t -> t.getActive()).forEach(t -> {
			
			client.getChannel(t.getChannel()).subscribe(new ClientSessionChannel.MessageListener() {

				@Override
				public void onMessage(ClientSessionChannel channel, Message message) {
					
					Long start = System.currentTimeMillis();
					
					LOGGER.info("**** start message received ****");
					
					com.nowellpoint.client.sforce.model.StreamingEvent source = null;
		
					try {
						source = com.nowellpoint.client.sforce.model.StreamingEvent.of(message.getDataAsMap());
					} catch (IOException e) {
						LOGGER.error(e);
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
					streamingEvent.setOrganizationId(new ObjectId(configuration.getOrganizationId()));
					streamingEvent.setReplayId(source.getEvent().getReplayId());
					streamingEvent.setType(source.getEvent().getType());
					streamingEvent.setSource(t.getSource());
					streamingEvent.setPayload(payload);
					
					try {
						MongoConnection.getInstance().getDatastore().save(streamingEvent);
					} catch (DuplicateKeyException e) {
						LOGGER.warn(e.getErrorMessage());
					}
					
					LOGGER.info("message processed in (ms): ".concat(String.valueOf(System.currentTimeMillis() - start)));
					LOGGER.info("**** end message received ****");
				}
			});
			
			LOGGER.info("Subscribed to channel: " + t.getChannel());
		});
	}
	
	public void connect() {
		
		httpClient = new HttpClient();
		httpClient.setConnectTimeout(CONNECTION_TIMEOUT);
		httpClient.setStopTimeout(STOP_TIMEOUT);
		httpClient.setIdleTimeout(READ_TIMEOUT);
		
        try {
			httpClient.start();
		} catch (Exception e) {
			LOGGER.error(e);
			return;
		}
		
		Token token = refreshToken(configuration.getRefreshToken());
		
		Map<String, Object> options = new HashMap<>();
		options.put(ClientTransport.MAX_NETWORK_DELAY_OPTION, READ_TIMEOUT);
		
		LongPollingTransport httpTransport = new LongPollingTransport(options, httpClient) {
            @Override
            protected void customize(Request request) {
            	super.customize(request);
                request.header("Authorization", "OAuth " + token.getAccessToken());
            }
        };
        
        ConcurrentMap<String, Long> dataMap = new ConcurrentHashMap<>();

        client = new BayeuxClient(token.getInstanceUrl().concat("/cometd/").concat(configuration.getApiVersion()), httpTransport);
        
        client.addExtension(new ClientSession.Extension() {
        	
        	@Override
            public boolean rcv(ClientSession session, Message.Mutable message) {
                configuration.getTopics().stream().forEach(t -> {
                	dataMap.put(t.getChannel(), Long.valueOf(-1));
                });
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
				subscribe();
				if (! message.isSuccessful()) {
					LOGGER.error(channel.getChannelId() + ": " + message.toString());
				}
			}
		});
        
        client.getChannel(Channel.META_CONNECT).addListener(new ClientSessionChannel.MessageListener() {
			
			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				if (! message.isSuccessful()) {
					LOGGER.error(channel.getChannelId() + ": " + message.toString());
				}
			}
		});
        
        client.getChannel(Channel.META_SUBSCRIBE).addListener(new ClientSessionChannel.MessageListener() {
			
			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				if (! message.isSuccessful()) {
					LOGGER.error(channel.getChannelId() + ": " + message.toString());
				}
			}
		});
        
        client.handshake();
        
        boolean connected = client.waitFor(TimeUnit.SECONDS.toMillis(60), BayeuxClient.State.CONNECTED);
        
        if (! connected) {
        	stopHttpClient();
        	LOGGER.error(String.format("%s failed to connect to the server at %s", this.getClass().getName(), client.getURL()));
        }
	}
}