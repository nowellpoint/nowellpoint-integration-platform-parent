package com.nowellpoint.content.service;

import java.util.Optional;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.nowellpoint.content.model.Config;

public class ConfigService extends S3ObjectService<Config> {
	
	public Optional<Config> getConfig(String name) {
		S3ObjectIdBuilder builder = new S3ObjectIdBuilder();
		builder.setBucket("nowellpoint-static-content");
		builder.setKey(name);
		
		GetObjectRequest request = new GetObjectRequest(builder.build());
		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		
		S3Object object = s3client.getObject(request);
		
		return readItem(Config.class, object);
	}
}