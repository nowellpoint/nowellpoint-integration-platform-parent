package com.nowellpoint.listener;

import java.io.IOException;

import org.jboss.logging.Logger;

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
	private static final String S3_BUCKET = "com.nowellpoint.configuration";
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public void start() {
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		ObjectListing objectListing = s3client.listObjects(S3_BUCKET);
		
		objectListing.getObjectSummaries().stream().forEach(os -> {
			S3ObjectIdBuilder builder = new S3ObjectIdBuilder();
			builder.setBucket(os.getBucketName());
			builder.setKey(os.getKey());
			
			GetObjectRequest request = new GetObjectRequest(builder.build());
			
			S3Object object = s3client.getObject(request);	
			
			try {
				new TopicSubscription(mapper.readValue(object.getObjectContent(), Configuration.class));
			} catch (IOException e) {
				logger.error(e);
			}
		});
	}
	
	public void stop() {
		
	}
}