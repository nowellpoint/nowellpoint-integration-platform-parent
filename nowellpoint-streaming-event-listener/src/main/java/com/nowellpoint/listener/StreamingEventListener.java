package com.nowellpoint.listener;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import javax.jms.JMSException;
//import javax.jms.Message;
//import javax.jms.MessageConsumer;
//import javax.jms.MessageListener;
//import javax.jms.Queue;
//import javax.jms.Session;
//import javax.jms.TextMessage;

import org.jboss.logging.Logger;

//import com.amazon.sqs.javamessaging.SQSConnection;
//import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.listener.model.Configuration;

public class StreamingEventListener {
	
	private static final Logger logger = Logger.getLogger(StreamingEventListener.class);
	private static final String S3_BUCKET = "streaming-event-listener-us-east-1-600862814314";
	//private static final String CONFIGURATION_QUEUE = "streaming-event-listener-configuration-events";
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Map<String,TopicSubscription> topicSubscriptions = new ConcurrentHashMap<>();
	
//	private static SQSConnectionFactory connectionFactory;
//	private static SQSConnection connection;
//	private static Session session;
	
	public void start() {
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		ObjectListing objectListing = s3client.listObjects(S3_BUCKET);
		
		logger.info(objectListing.getObjectSummaries().size());
		
		objectListing.getObjectSummaries().stream().filter(os -> os.getSize() > 0).forEach(os -> {
			S3ObjectIdBuilder builder = new S3ObjectIdBuilder();
			builder.setBucket(os.getBucketName());
			builder.setKey(os.getKey());
			
			GetObjectRequest request = new GetObjectRequest(builder.build());
			
			S3Object object = s3client.getObject(request);	
			
			try {
				topicSubscriptions.put(os.getKey(), new TopicSubscription(mapper.readValue(object.getObjectContent(), Configuration.class)));
			} catch (IOException e) {
				logger.error(e);
			}
		});
		
//    	try {
//			Queue queue = session.createQueue(CONFIGURATION_QUEUE);
//			MessageConsumer consumer = session.createConsumer(queue);
//			MessageListener listener = new StreamingEventMessageListener();
//			
//			consumer.setMessageListener(listener);
//				 				
//		} catch (JMSException e) {
//			logger.error(e);
//		}
	}
	
	public void stop() {
		topicSubscriptions.keySet().stream().forEach(k -> {
			topicSubscriptions.get(k).disconnect();
			topicSubscriptions.remove(k);
		});
	}
	
//	class StreamingEventMessageListener implements MessageListener {
//
//		@Override
//		public void onMessage(Message message) {
//			TextMessage textMessage = (TextMessage) message;
//			try {
//				textMessage.getText();
//			} catch (JMSException e) {
//				logger.error(e);
//			}
//			//topicSubscriptions.get("");
//			
//		}
//	}
}