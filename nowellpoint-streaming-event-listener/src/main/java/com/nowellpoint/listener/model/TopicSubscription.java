package com.nowellpoint.listener.model;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
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

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.listener.StreamingEventListener;
import com.nowellpoint.listener.connection.MongoConnection;
import com.nowellpoint.util.SecretsManager;
import com.nowellpoint.util.SecureValue;
import com.nowellpoint.util.SecureValueException;

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
	
	private boolean connected;

	@Builder
	private TopicSubscription(TopicConfiguration configuration) {
		this.configuration = configuration;
		this.connected = Boolean.FALSE;
		connect();
		if (connected) {
			subscribe();
		}
	}
	
	private Token refreshToken(String refreshToken) {
		
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
				.setRefreshToken(refreshToken)
				.build();
		
		OauthAuthenticationResponse response = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		return response.getToken();
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
		if (connected) {
			configuration.getTopics().stream().forEach( t -> {
				client.getChannel(t.getChannel()).unsubscribe();
			});
			client.disconnect();
			client.waitFor(TimeUnit.SECONDS.toMillis(60), BayeuxClient.State.DISCONNECTED);
		}
		
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
					
					Document payload = new Document()
							.append("id", source.getSobject().getId())
							.append("name", source.getSobject().getName())
							.append("createdById", source.getSobject().getCreatedById())
							.append("createdDate", source.getSobject().getCreatedDate())
							.append("lastModifiedById", source.getSobject().getLastModifiedById())
							.append("lastModifiedDate", source.getSobject().getLastModifiedDate());
					
					Document streamingEvent = new Document()
							.append("eventDate", source.getEvent().getCreatedDate())
							.append("organizationId", new ObjectId(configuration.getOrganizationId()))
							.append("replayId", source.getEvent().getReplayId())
							.append("type", source.getEvent().getType())
							.append("source", t.getSource())
							.append("payload", payload);
					
					try {
						writeStreamingEvent(streamingEvent);
					} catch (MongoWriteException e) {
		                if (e.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
		                	LOGGER.warn(e.getMessage());
		                }
					}
					
					LOGGER.info("message processed in (ms): ".concat(String.valueOf(System.currentTimeMillis() - start)));
					LOGGER.info("**** end message received ****");
				}
			});
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
		
		Token token;
		
		try {
			token = refreshToken(SecureValue.decryptBase64(configuration.getRefreshToken()));
		} catch (OauthException | SecureValueException e) {
			LOGGER.error("Unable to connect to organization: " + configuration.getOrganizationId() + " (" + e.getMessage() + ")");
			return;
		}
		
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
					// TODO: reconnect
				}
			}
		});
        
        client.getChannel(Channel.META_SUBSCRIBE).addListener(new ClientSessionChannel.MessageListener() {
			
			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				if (message.isSuccessful()) {
					
					LOGGER.info("Subscribed to channel: " + message.toString());
					
					Document notification = new Document()
							.append("isRead", Boolean.FALSE)
							.append("isUrgent", Boolean.FALSE)
							.append("message", message.toString())
							.append("organizationId", new ObjectId(configuration.getOrganizationId()))
							.append("receivedOn", new Date())
							.append("subject", "Subscribed to channel")
							.append("receivedFrom", "StreamingEventListener");
					
					writeNotification(notification);
					
				} else {
					
					LOGGER.error("Unable to subscribe to channel: " + message.toString());
					
					Document notification = new Document()
							.append("isRead", Boolean.FALSE)
							.append("isUrgent", Boolean.TRUE)
							.append("message", message.toString())
							.append("organizationId", new ObjectId(configuration.getOrganizationId()))
							.append("receivedOn", new Date())
							.append("subject", "Unable to subscribe to channel")
							.append("receivedFrom", "StreamingEventListener");
					
					writeNotification(notification);
				}
			}
		});
        
        client.handshake();
        
        connected = client.waitFor(TimeUnit.SECONDS.toMillis(60), BayeuxClient.State.CONNECTED);
        
        if (! connected) {
        	
        	stopHttpClient();
        	
        	String error = String.format("%s failed to connect to the server at %s", this.getClass().getName(), client.getURL());
        	
        	LOGGER.error(error);
        	
        	Document notification = new Document()
					.append("isRead", Boolean.FALSE)
					.append("isUrgent", Boolean.TRUE)
					.append("message", error)
					.append("organizationId", new ObjectId(configuration.getOrganizationId()))
					.append("receivedOn", new Date())
					.append("subject", "Unable to connect")
					.append("receivedFrom", "StreamingEventListener");
			
			writeNotification(notification);
        }
	}
	
	private void writeNotification(Document notification) {
		MongoConnection.getInstance().getMongoDatabase().getCollection("notifications").insertOne(notification);
	}
	
	private void writeStreamingEvent(Document streamingEvent) {
		MongoConnection.getInstance().getMongoDatabase().getCollection("streaming.events").insertOne(streamingEvent);
	}
}