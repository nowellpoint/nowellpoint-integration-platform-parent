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

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazon.sqs.javamessaging.SQSConnection;
//import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.listener.model.Configuration;

public class StreamingEventListener {
	
	private static StreamingEventListener instance = new StreamingEventListener();
	
	private static final Logger logger = Logger.getLogger(StreamingEventListener.class);
	private static final String BUCKET = "streaming-event-listener-us-east-1-600862814314";
	private static final String PREFIX = "configuration/";
	//private static final String CONFIGURATION_QUEUE = "streaming-event-listener-configuration-events";
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final Map<String,TopicSubscription> topicSubscriptions = new ConcurrentHashMap<>();
	
//	private static SQSConnectionFactory connectionFactory;
//	private static SQSConnection connection;
//	private static Session session;
	
	private StreamingEventListener() {}
	
	public void start() {
		
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		ObjectListing objectListing = s3client.listObjects(new ListObjectsRequest().withBucketName(BUCKET).withPrefix(PREFIX));
		
		try {
			logger.info(mapper.writeValueAsString(objectListing));
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		logger.info(objectListing.getObjectSummaries().size());
		
		objectListing.getObjectSummaries().stream().filter(os -> os.getSize() > 0).forEach(os -> {
			S3ObjectIdBuilder builder = new S3ObjectIdBuilder();
			builder.setBucket(os.getBucketName());
			builder.setKey(os.getKey());
			
			logger.info(os.getKey());
			
			GetObjectRequest request = new GetObjectRequest(builder.build());
			
			S3Object object = s3client.getObject(request);	
			
			try {
				topicSubscriptions.put(os.getKey(), new TopicSubscription(mapper.readValue(object.getObjectContent(), Configuration.class)));
			} catch (IOException e) {
				logger.error(e);
			}
		});
		
		ListObjectsV2Request request = new ListObjectsV2Request().withBucketName("nowellpoint-profile-photos").withMaxKeys(2);
        ListObjectsV2Result result;
        
        try {
			logger.info(mapper.writeValueAsString(request));
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        do {
            result = s3client.listObjectsV2(request);
            
            try {
    			logger.info(mapper.writeValueAsString(result));
    		} catch (JsonProcessingException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
            
            logger.info(result.getObjectSummaries().size());

            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                System.out.printf(" - %s (size: %d)\n", objectSummary.getKey(), objectSummary.getSize());
            }
            // If there are more than maxKeys keys in the bucket, get a continuation token
            // and list the next objects.
            String token = result.getNextContinuationToken();
            System.out.println("Next Continuation Token: " + token);
            request.setContinuationToken(token);
        } while (result.isTruncated());
        
        S3ObjectIdBuilder builder = new S3ObjectIdBuilder();
		builder.setBucket(BUCKET);
		builder.setKey("");
		
		GetObjectRequest getObjectRequest = new GetObjectRequest(builder.build());
		
		
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
	
	public static StreamingEventListener getInstance() {
		return instance;
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
