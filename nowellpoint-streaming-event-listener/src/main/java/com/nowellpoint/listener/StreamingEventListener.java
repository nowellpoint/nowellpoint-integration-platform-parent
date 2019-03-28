package com.nowellpoint.listener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.jboss.logging.Logger;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.nowellpoint.listener.connection.MongoConnection;
import com.nowellpoint.listener.model.TopicConfiguration;
import com.nowellpoint.listener.model.TopicSubscription;
import com.nowellpoint.util.Properties;

@WebListener
public class StreamingEventListener extends Cache implements ServletContextListener {
	
	private static final Logger LOGGER = Logger.getLogger(StreamingEventListener.class);
	private static final Jsonb JSON_BUILDER = JsonbBuilder.create(getJsonbConfig());
	
	private final AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
	
	private SQSConnectionFactory connectionFactory;
	private SQSConnection connection;
	private Session session;
	
	@Override
    public void contextInitialized(ServletContextEvent event) {    
		mongoConnect();
		startListeners();
        startQueue();
	}
	
	@Override
    public void contextDestroyed(ServletContextEvent event) {
		stopQueue();
		disconnectAll();
		mongoDisconnect();
	}
	
	private void mongoConnect() {
		MongoConnection.getInstance().connect();
	}
	
	private void mongoDisconnect() {
		MongoConnection.getInstance().disconnect();
	}
	
	private void startListeners() {
		
		String bucketName = System.getProperty(Properties.STREAMING_EVENT_LISTENER_BUCKET);
		String prefix = "configuration/"
				.concat(System.getProperty(Properties.STREAMING_EVENT_LISTENER_QUEUE))
				.concat("/");
		
		ListObjectsV2Request request = new ListObjectsV2Request()
				.withBucketName(bucketName)
				.withPrefix(prefix)
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
	}
	
	private void startQueue() {
		try {
    		connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), AmazonSQSClientBuilder.defaultClient());
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            Queue queue = session.createQueue(System.getProperty(Properties.STREAMING_EVENT_LISTENER_QUEUE));
            MessageConsumer messageConsumer = session.createConsumer(queue);
            
            messageConsumer.setMessageListener(new MessageListener() {
				
				@Override
				public void onMessage(Message message) {
					TextMessage textMessage = (TextMessage) message;
					try {
						
						S3EventNotification event = S3EventNotification.parseJson(textMessage.getText());
						
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
						
					} catch (JMSException e) {
						LOGGER.error(e);
					}	
				}
			});
			
			connection.start();
				 				
		} catch (JMSException e) {
			LOGGER.error(e);
		} 
	}
	
	private void stopQueue() {
		try {
			session.close();
			connection.stop();
		} catch (JMSException e) {
			LOGGER.error(e);
		}
	}
	
	private TopicConfiguration readConfiguration(S3Object s3object) {
		return JSON_BUILDER.fromJson(s3object.getObjectContent(), TopicConfiguration.class);
	}
	
	private static JsonbConfig getJsonbConfig() {
		return new JsonbConfig()
				.withNullValues(Boolean.TRUE)
				.withPropertyVisibilityStrategy(
						new PropertyVisibilityStrategy() {
							
							@Override
							public boolean isVisible(Field field) {
								return true;
							}
							
							@Override
							public boolean isVisible(Method method) {
								return false;
							}
							
						});
	}
}