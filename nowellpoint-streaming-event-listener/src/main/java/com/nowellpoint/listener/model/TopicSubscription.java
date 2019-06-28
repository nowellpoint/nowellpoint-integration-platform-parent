package com.nowellpoint.listener.model;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

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

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.util.SecureValue;
import com.nowellpoint.util.SecureValueException;

import lombok.Builder;

public class TopicSubscription extends AbstractTopicSubscription {
	
	private static final Logger LOGGER = Logger.getLogger(TopicSubscription.class);
	
	private static final String REPLAY = "replay";
	private static final String CHANNEL = "/data/ChangeEvents";
	private static final String QUEUE = "change.event.listener.queue";
	
	private static final int CONNECTION_TIMEOUT = 20 * 1000;
    private static final int READ_TIMEOUT = 120 * 1000; 
    private static final int STOP_TIMEOUT = 120 * 1000;

	private HttpClient httpClient;
	private BayeuxClient client;
	
	private boolean connected;

	@Builder
	private TopicSubscription(TopicConfiguration configuration) {
		this.connected = Boolean.FALSE;
		connect(configuration);
	}
	
	public void reconnect(TopicConfiguration configuration) {
		stopListener();
		connect(configuration);
	}
	
	public void stopListener() {
		if (connected) {
//			configuration.getTopics().stream().forEach( t -> {
//				client.getChannel(t.getChannel()).unsubscribe();
//			});
			client.getChannel(CHANNEL).unsubscribe();
			client.disconnect();
			client.waitFor(TimeUnit.SECONDS.toMillis(60), BayeuxClient.State.DISCONNECTED);
		}
		
		stopHttpClient();
	}
	
	private void subscribe(TopicConfiguration configuration) {
		
//		configuration.getTopics().stream().filter(t -> t.getActive()).forEach(t -> {
//			
//			client.getChannel(t.getChannel()).subscribe(new ClientSessionChannel.MessageListener() {
//
//				@Override
//				public void onMessage(ClientSessionChannel channel, Message message) {
//					
//					LOGGER.info(String.format("Message received for organization: %s from %s", 
//							configuration.getOrganizationId(), 
//							message.getChannel()));
		
//					try {
//						
//						com.nowellpoint.client.sforce.model.StreamingEvent source = 
//								com.nowellpoint.client.sforce.model.StreamingEvent.of(message.getDataAsMap());
//						
//						Document payload = new Document()
//								.append("id", source.getSobject().getId())
//								.append("name", source.getSobject().getName())
//								.append("createdById", source.getSobject().getCreatedBy().getId())
//								.append("createdDate", source.getSobject().getCreatedDate())
//								.append("lastModifiedById", source.getSobject().getLastModifiedBy().getId())
//								.append("lastModifiedDate", source.getSobject().getLastModifiedDate());
//						
//						Document streamingEvent = new Document()
//								.append("eventDate", source.getEvent().getCreatedDate())
//								.append("organizationId", new ObjectId(configuration.getOrganizationId()))
//								.append("replayId", source.getEvent().getReplayId())
//								.append("type", source.getEvent().getType())
//								.append("source", t.getSource())
//								.append("payload", payload);
//						
//						writeStreamingEvent(streamingEvent);
//						
//					} catch (IOException e) {
//						LOGGER.error(e);
//					}					
//				}
//			});
//		});
		
		
		client.getChannel(CHANNEL).subscribe(new ClientSessionChannel.MessageListener() {
			
			final Jsonb jsonb = JsonbBuilder.create();
			final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				
				long start = System.currentTimeMillis();
				
				LOGGER.info(String.format("Message received for organization: %s from %s", 
						configuration.getOrganizationId(), 
						message.getChannel()));
				
				try {
					
					com.nowellpoint.client.sforce.model.changeevent.ChangeEvent source = 
							com.nowellpoint.client.sforce.model.changeevent.ChangeEvent.of(message.getDataAsMap());
					
					Event event = Event.builder()
							.replayId(source.getEvent().getReplayId())
							.build();
					
					ChangeEventHeader changeEventHeader = ChangeEventHeader.builder()
							.changeOrigin(source.getPayload().getChangeEventHeader().getChangeOrigin())
							.changeType(source.getPayload().getChangeEventHeader().getChangeType())
							.commitNumber(source.getPayload().getChangeEventHeader().getCommitNumber())
							.commitTimestamp(source.getPayload().getChangeEventHeader().getCommitTimestamp())
							.commitUser(source.getPayload().getChangeEventHeader().getCommitUser())
							.entityName(source.getPayload().getChangeEventHeader().getEntityName())
							.recordIds(source.getPayload().getChangeEventHeader().getRecordIds())
							.sequenceNumber(source.getPayload().getChangeEventHeader().getSequenceNumber())
							.transactionKey(source.getPayload().getChangeEventHeader().getTransactionKey())
							.build();
					
					Payload payload = Payload.builder()
							.attributes(source.getPayload().getAttributes())
							.changeEventHeader(changeEventHeader)
							.lastModifiedDate(source.getPayload().getLastModifiedDate())
							.build();
					
					ChangeEvent changeEvent = ChangeEvent.builder()
							.event(event)
							.organizationId(configuration.getOrganizationId())
							.payload(payload)
							.schema(source.getSchema())
							.build();
					
					final Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
					messageAttributes.put("Token", new MessageAttributeValue()
					        .withDataType("String")
					        .withStringValue(configuration.getRefreshToken()));
					
					final SendMessageRequest sendMessageRequest = new SendMessageRequest()
							.withMessageAttributes(messageAttributes)
							.withMessageBody(jsonb.toJson(changeEvent))
							.withMessageDeduplicationId(changeEvent.getOrganizationId().concat("-").concat(String.valueOf(event.getReplayId())))
							.withMessageGroupId(changeEventHeader.getCommitUser())
							.withQueueUrl(System.getProperty(QUEUE));
					
					sqs.sendMessage(sendMessageRequest);
					
				} catch (IOException e) {
					LOGGER.error(e);
				}
					
				
				System.out.println(System.currentTimeMillis() - start);
			}
		});
	}
	
	private void connect(TopicConfiguration configuration) {
		
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
		
		try {
			
			Token token = refreshToken(SecureValue.decryptBase64(configuration.getRefreshToken()));
			
			Map<String, Object> options = new HashMap<>();
			options.put(ClientTransport.MAX_NETWORK_DELAY_OPTION, READ_TIMEOUT);
			
			LongPollingTransport httpTransport = new LongPollingTransport(options, httpClient) {
	            @Override
	            protected void customize(Request request) {
	            	super.customize(request);
	                request.header("Authorization", "OAuth " + token.getAccessToken());
	            }
	        };
	        
	        client = new BayeuxClient(token.getInstanceUrl().concat("/cometd/").concat(configuration.getApiVersion()), httpTransport);
			
		} catch (OauthException | SecureValueException e) {
			
			LOGGER.error("Unable to connect to organization: " + configuration.getOrganizationId() + " (" + e.getMessage() + ")");
			
			Document notification = new Document()
					.append("isRead", Boolean.FALSE)
					.append("isUrgent", Boolean.TRUE)
					.append("message", "Unable to connect to organization: " + configuration.getOrganizationId() + " (" + e.getMessage() + ")")
					.append("organizationId", new ObjectId(configuration.getOrganizationId()))
					.append("receivedOn", new Date())
					.append("subject", "Unable to subscribe to channel")
					.append("receivedFrom", "ChangeEventListener");
			
			writeNotification(notification);
			
			return;
		}

        ConcurrentMap<String, Long> dataMap = new ConcurrentHashMap<>();
        
        client.addExtension(new ClientSession.Extension() {
        	
        	@Override
            public boolean rcv(ClientSession session, Message.Mutable message) {
//                configuration.getTopics().stream().forEach(t -> {
//                	dataMap.put(t.getChannel(), Long.valueOf(-1));
//                });
                dataMap.put(CHANNEL, getReplayId(configuration.getOrganizationId()));
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
				subscribe(configuration);
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
        
        if (connected) {
        	subscribe(configuration);
        } else {	
        	
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
	
	private void stopHttpClient() {
		try {
			httpClient.stop();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}
}