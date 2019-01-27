package com.nowellpoint.listener;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.listener.connection.MongoConnection;
import com.nowellpoint.listener.model.S3Event;
import com.nowellpoint.listener.model.TopicConfiguration;

public class StreamingEventListener extends AbstractTopicSubscriptionManager {
	
	private static StreamingEventListener instance = new StreamingEventListener();
	
	private static final Logger LOGGER = Logger.getLogger(StreamingEventListener.class);
	private static final String BUCKET = "streaming-event-listener-us-east-1-600862814314";
	private static final String PREFIX = "configuration/";
	private static final String QUEUE = "streaming-event-listener-configuration-events";
	
	private final ObjectMapper mapper = new ObjectMapper();
	private final AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
	
	private SQSConnectionFactory connectionFactory;
	private SQSConnection connection;
	private Session session;
	
	private StreamingEventListener() {}
	
	public void start() {
		
		mongoConnect();
		
		ListObjectsV2Request request = new ListObjectsV2Request()
				.withBucketName(BUCKET)
				.withPrefix(PREFIX)
				.withMaxKeys(1000);
		
        ListObjectsV2Result result = null;

        do {
        	result = s3client.listObjectsV2(request);
            
        	result.getObjectSummaries().stream().filter(os -> os.getSize() > 0).forEach(os -> {
        		
    			S3ObjectIdBuilder builder = new S3ObjectIdBuilder()
    					.withBucket(os.getBucketName())
    					.withKey(os.getKey());
    			
    			S3Object s3object = s3client.getObject(new GetObjectRequest(builder.build()));	
    			
    			TopicConfiguration configuration = readConfiguration(s3object);
    			
    			TopicSubscription subscription = TopicSubscription.builder()
    					.configuration(configuration)
    					.build();
    			
    			put(os.getKey(), subscription);
    		});
            
        	request.setContinuationToken(result.getNextContinuationToken());
			
        } while (result.isTruncated());
		
    	try {
    		connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), AmazonSQSClientBuilder.defaultClient());
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
			session.createConsumer(session.createQueue(QUEUE)).setMessageListener(new MessageListener() {
				
				@Override
				public void onMessage(Message message) {
					TextMessage textMessage = (TextMessage) message;
					try {
						S3Event event = mapper.readValue(textMessage.getText(), S3Event.class);
						
						event.getRecords().stream().forEach(record -> {
							
							String key = record.getS3().getObject().getKey();
							
							disconnect(key);
							
							S3ObjectIdBuilder builder = new S3ObjectIdBuilder()
									.withBucket(record.getS3().getBucket().getName())
									.withKey(key);
							
							S3Object s3object = s3client.getObject(new GetObjectRequest(builder.build()));
							
							TopicConfiguration configuration = readConfiguration(s3object);
							
							reconnect(key, configuration);
						});
						
						message.acknowledge();
						
					} catch (JMSException | IOException e) {
						LOGGER.error(e);
					}	
				}
			});
			
			connection.start();
				 				
		} catch (JMSException e) {
			LOGGER.error(e);
		} 
	}
	
	public void stop() {
		stopQueue();
		disconnectAll();
		mongoDisconnect();
	}
	
	public static StreamingEventListener getInstance() {
		return instance;
	}
	
	private void mongoConnect() {
		MongoConnection.getInstance().connect();
	}
	
	private void mongoDisconnect() {
		MongoConnection.getInstance().disconnect();
	}
	
	private void stopQueue() {
		try {
			session.close();
			connection.stop();
		} catch (JMSException e) {
			LOGGER.error(e);
		}
	}
}