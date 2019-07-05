package com.nowellpoint.listener;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.nowellpoint.listener.model.TopicConfiguration;
import com.nowellpoint.listener.model.TopicSubscription;
import com.nowellpoint.listener.util.CacheManager;

public class TopicChangeMessageListener implements MessageListener {
	
	private final AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();

	@Inject
	private Logger logger;
	
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			
			S3EventNotification event = S3EventNotification.parseJson(textMessage.getText());
			
			event.getRecords().stream().forEach(record -> {
				
				String key = record.getS3().getObject().getKey();
				
				S3ObjectIdBuilder builder = new S3ObjectIdBuilder()
						.withBucket(record.getS3().getBucket().getName())
						.withKey(key);
				
				S3Object s3object = s3client.getObject(new GetObjectRequest(builder.build()));
				
				TopicConfiguration configuration = TopicConfiguration.of(s3object);
				
				if (containsKey(key)) {
					TopicSubscription subscription = get(key);
					subscription.stopListener();
					subscription.reconnect(configuration);
				} else {
	    			
	    			Long replayId = Long.valueOf(-1);
	    			
	    			TopicSubscription subscription = TopicSubscription.builder()
	    					.configuration(configuration)
	    					.replayId(replayId)
	    					.build();
	    			
					put(key, subscription);
				}
			});
			
			message.acknowledge();
			
		} catch (JMSException e) {
			logger.error(e);
		}	
	}
	
	private Boolean containsKey(String key) {
		return CacheManager.getTopicSubscriptions().containsKey(key);
	}
	
	private TopicSubscription get(String key) {
		return CacheManager.getTopicSubscriptions().get(key);
	}
	
	private void put(String key, TopicSubscription topicSubscription) {
		CacheManager.getTopicSubscriptions().put(key, topicSubscription);
	}
}