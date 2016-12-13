package com.nowellpoint.api.model.dynamodb;

import java.io.IOException;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CallbackTypeConverter implements DynamoDBTypeConverter<String, List<Callback>> {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convert(List<Callback> results) {
		try {
            return objectMapper.writeValueAsString(results);
        } catch (JsonProcessingException e) {
        	e.printStackTrace();
        	return null;
        }
	}

	@Override
	public List<Callback> unconvert(String json) {
		try {
			return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Callback.class));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}