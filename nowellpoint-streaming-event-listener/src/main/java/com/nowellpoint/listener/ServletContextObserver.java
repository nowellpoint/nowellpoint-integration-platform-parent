package com.nowellpoint.listener;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.servlet.ServletContext;

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
import com.nowellpoint.listener.model.QueueConfiguration;
import com.nowellpoint.listener.model.TopicConfiguration;
import com.nowellpoint.listener.model.TopicSubscription;
import com.nowellpoint.listener.util.CacheManager;
import com.nowellpoint.listener.util.JsonbUtil;
import com.nowellpoint.util.Properties;

@ApplicationScoped
public class ServletContextObserver {
	
	private static final String QUEUE_CONFIGURATION_BUCKET = "queue.configuration.bucket";
	private static final String QUEUE_CONFIGURATION_FILE = "queue-configuration.json";
	
	private final AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
	
	private SQSConnectionFactory connectionFactory;
	private SQSConnection connection;
	private Session session;
	
	@Inject
	private Logger logger;
	
	public void start(@Observes @Initialized(value = ApplicationScoped.class) ServletContext context) {
		List<QueueConfiguration> configurations = getQueueConfigurations();
		startQueues(configurations);
		startListeners(configurations);
	}
	
	public void stop(@Observes @Destroyed(value = ApplicationScoped.class) ServletContext context) {
		stopListeners();
		stopQueues();
	}
	
	private void startQueues(List<QueueConfiguration> configurations) {		
		try {
    		connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), AmazonSQSClientBuilder.defaultClient());
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            for (QueueConfiguration configuration : configurations) {
            	Queue queue = session.createQueue(configuration.getQueueName());
                MessageConsumer messageConsumer = session.createConsumer(queue);
                try {
                	MessageListener listener = toMessageListener(configuration.getListenerClass());
                	messageConsumer.setMessageListener(listener);
                } catch (IllegalArgumentException e) {
                	logger.error(e);
                }
            };
			
			connection.start();
				 				
		} catch (ClassNotFoundException | JMSException e) {
			logger.error(e);
		} 
	}
	
	private void startListeners(List<QueueConfiguration> configurations) {
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
    			
    			TopicConfiguration configuration = TopicConfiguration.of(s3object);
    			
    			Long replayId = Long.valueOf(-1);
    			
    			TopicSubscription subscription = TopicSubscription.builder()
    					.configuration(configuration)
    					.replayId(replayId)
    					.build();
    			
    			put(os.getKey(), subscription);
    		});
            
        	request.setContinuationToken(result.getNextContinuationToken());
			
        } while (result.isTruncated());
	}
	
	private void stopQueues() {
		try {
			session.close();
			connection.stop();
		} catch (JMSException e) {
			logger.error(e);
		}
	}
	
	private void stopListeners() {
		CacheManager.getTopicSubscriptionCache().keySet().stream().forEach(k -> {
			CacheManager.getTopicSubscriptionCache().get(k).stopListener();
		});
		CacheManager.getTopicSubscriptionCache().clear();
	}
	
	private void put(String key, TopicSubscription topicSubscription) {
		CacheManager.getTopicSubscriptionCache().put(key, topicSubscription);
	}
	
	private MessageListener toMessageListener(String className) throws ClassNotFoundException {
		logger.info("registering message listener: " + className);
		Class<?> clazz = Class.forName(className);
		Object object = CDI.current().select(clazz).get();
		if (object instanceof MessageListener) {
			return MessageListener.class.cast(object);
		}
		CDI.current().destroy(object);
		throw new IllegalArgumentException(String.format("Class %s must implement type MessageListener", className));
	}
	
	@SuppressWarnings("serial")
	private List<QueueConfiguration> getQueueConfigurations() {
		String bucketName = System.getProperty(QUEUE_CONFIGURATION_BUCKET);
		
		S3ObjectIdBuilder builder = new S3ObjectIdBuilder()
				.withBucket(bucketName)
				.withKey(QUEUE_CONFIGURATION_FILE);
		
		GetObjectRequest getObjectRequest = new GetObjectRequest(builder.build());
		
		S3Object s3object = s3client.getObject(getObjectRequest);
		
		return JsonbUtil.getJsonb().fromJson(s3object.getObjectContent(), new ArrayList<QueueConfiguration>(){}.getClass().getGenericSuperclass());
	}
}