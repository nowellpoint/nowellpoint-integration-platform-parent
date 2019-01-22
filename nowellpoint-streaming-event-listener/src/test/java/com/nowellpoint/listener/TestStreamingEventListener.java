package com.nowellpoint.listener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestStreamingEventListener {
	
	private static final String BUCKET = "streaming-event-listener-us-east-1-600862814314";
	private static final String KEY = "configuration/5bac3c0e0626b951816064f5";

	@Test
	public void testTopicConfigurationChange() throws JsonParseException, JsonMappingException, IOException {
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		S3ObjectIdBuilder builder = new S3ObjectIdBuilder()
				.withBucket(BUCKET)
				.withKey(KEY);
		
		S3Object object = s3client.getObject(new GetObjectRequest(builder.build()));	
		
		JsonNode node = new ObjectMapper().readValue(object.getObjectContent(), JsonNode.class);
		
		byte[] bytes = node.toString().getBytes(StandardCharsets.UTF_8);
		InputStream input = new ByteArrayInputStream(bytes);
		
		ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        metadata.setContentLength(bytes.length);
		
		PutObjectRequest request = new PutObjectRequest(BUCKET, KEY, input, metadata);
        
        s3client.putObject(request);
	}
}