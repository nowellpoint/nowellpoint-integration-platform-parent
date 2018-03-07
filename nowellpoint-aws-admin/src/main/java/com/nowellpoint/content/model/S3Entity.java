package com.nowellpoint.content.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import com.amazonaws.services.s3.model.S3Object;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class S3Entity<T> {
	
	protected static final ObjectMapper mapper = new ObjectMapper();
	
	protected List<T> getCollection(Class<T> type, S3Object object) {
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

}