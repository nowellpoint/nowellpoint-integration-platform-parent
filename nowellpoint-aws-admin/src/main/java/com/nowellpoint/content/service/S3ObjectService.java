package com.nowellpoint.content.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class S3ObjectService {
	
	protected static final ObjectMapper mapper = new ObjectMapper();
	
	protected static final AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
	
	protected <T> List<T> readCollection(Class<T> type, S3Object object) {
		List<T> items = Collections.emptyList();
		
		InputStream inputStream = object.getObjectContent();
		
		JavaType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, type);
		
		try {
			items = mapper.readValue(inputStream, collectionType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	protected <T> Optional<T> readItem(Class<T> type, S3Object object) {
		Optional<T> item = Optional.empty();
		
		InputStream inputStream = object.getObjectContent();
		
		try {
			item = Optional.of(mapper.readValue(inputStream, type));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return item;
	}
}