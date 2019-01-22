package com.nowellpoint.listener;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
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

public class StreamingEventListener {
	
	private static StreamingEventListener instance = new StreamingEventListener();
	
	private static final Logger LOGGER = Logger.getLogger(StreamingEventListener.class);
	private static final String BUCKET = "streaming-event-listener-us-east-1-600862814314";
	private static final String PREFIX = "configuration/";
	private static final String QUEUE = "streaming-event-listener-configuration-events";
	private static final Map<String,TopicSubscription> TOPIC_SUBSCRIPTIONS = new ConcurrentHashMap<>();
	
	private final ObjectMapper mapper = new ObjectMapper();
	
	private SQSConnectionFactory connectionFactory;
	private SQSConnection connection;
	private Session session;
	
	private StreamingEventListener() {
		
	}
	
	public void start() {
		
		final AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
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
    			
    			S3Object object = s3client.getObject(new GetObjectRequest(builder.build()));	
    			
    			try {
    				
    				addTopicSubscription(os.getKey(), new TopicSubscription(
    						mapper.readValue(object.getObjectContent(), TopicConfiguration.class), 
    						MongoConnection.getInstance().getDatastore()));
    				
    			} catch (IOException e) {
    				LOGGER.error(e);
    			}
    		});
            
        	request.setContinuationToken(result.getNextContinuationToken());
			
        } while (result.isTruncated());
		
    	try {
    		connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), AmazonSQSClientBuilder.defaultClient());
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
			Queue queue = session.createQueue(QUEUE);
			
			MessageConsumer consumer = session.createConsumer(queue);			
			consumer.setMessageListener(new StreamingEventMessageListener());
			
			connection.start();
				 				
		} catch (JMSException e) {
			LOGGER.error(e);
		} 
    	
    	mongoConnect();
	}
	
	public void stop() {
		stopQueue();
		disconnectTopics();
		mongoDisconnect();
	}
	
	public static StreamingEventListener getInstance() {
		return instance;
	}
	
	private void addTopicSubscription(String key, TopicSubscription topicSubscription) {
		TOPIC_SUBSCRIPTIONS.put(key, topicSubscription);
	}
	
	private void mongoConnect() {
		MongoConnection.getInstance().connect();
	}
	
	private void mongoDisconnect() {
		MongoConnection.getInstance().disconnect();
	}
	
	private void disconnectTopics() {
		TOPIC_SUBSCRIPTIONS.keySet().stream().forEach(k -> {
			TOPIC_SUBSCRIPTIONS.get(k).disconnect();
		});
		TOPIC_SUBSCRIPTIONS.clear();
	}
	
	private void stopQueue() {
		try {
			session.close();
			connection.stop();
		} catch (JMSException e) {
			LOGGER.error(e);
		}
	}
	
	class StreamingEventMessageListener implements MessageListener {

		@Override
		public void onMessage(Message message) {
			TextMessage textMessage = (TextMessage) message;
			try {
				S3Event event = new ObjectMapper().readValue(textMessage.getText(), S3Event.class);
				
				AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
				
				String key = event.getRecords().get(0).getS3().getObject().getKey();
				
				S3ObjectIdBuilder builder = new S3ObjectIdBuilder()
						.withBucket(event.getRecords().get(0).getS3().getBucket().getName())
						.withKey(key);
				
				S3Object object = s3client.getObject(new GetObjectRequest(builder.build()));
				
				TopicConfiguration topicConfiguration = new ObjectMapper().readValue(object.getObjectContent(), TopicConfiguration.class);
				
				LOGGER.info(topicConfiguration.getOrganizationId());
				
				TOPIC_SUBSCRIPTIONS.get(key).disconnect();
			
				TOPIC_SUBSCRIPTIONS.replace(key, new TopicSubscription(
						mapper.readValue(object.getObjectContent(), TopicConfiguration.class), 
						MongoConnection.getInstance().getDatastore()));
			
				message.acknowledge();
			} catch (JMSException | IOException e) {
				LOGGER.error(e);
			}
			//topicSubscriptions.get("");
			
		}
	}
}
