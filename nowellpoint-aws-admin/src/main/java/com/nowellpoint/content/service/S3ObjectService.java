package com.nowellpoint.content.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;

public abstract class S3ObjectService<T> {
	
	protected static final ObjectMapper mapper = new ObjectMapper();
	
	protected List<T> readCollection(Class<T> type, S3Object object) {
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
	
	protected Map<String,String> readMap() {
		Map<String,String> map = MapSerializ
	}
	
	protected Optional<T> readItem(Class<T> type, S3Object object) {
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