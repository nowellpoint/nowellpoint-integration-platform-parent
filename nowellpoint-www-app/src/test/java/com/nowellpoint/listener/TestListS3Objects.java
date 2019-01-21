package com.nowellpoint.listener;

import org.junit.Test;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestListS3Objects {
	
	@Test
	public void testListS3Objects() {
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		ObjectListing objectListing = s3client.listObjects(new ListObjectsRequest().withBucketName("streaming-event-listener-us-east-1-600862814314").withPrefix("configuration"));
		
		try {
			System.out.println(new ObjectMapper().writeValueAsString(objectListing));
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println(objectListing.getObjectSummaries().size());
		
		objectListing.getObjectSummaries().stream().filter(os -> os.getSize() > 0).forEach(os -> {
			S3ObjectIdBuilder builder = new S3ObjectIdBuilder();
			builder.setBucket(os.getBucketName());
			builder.setKey(os.getKey());
			
			System.out.println(os.getKey());
		});	
	}
}