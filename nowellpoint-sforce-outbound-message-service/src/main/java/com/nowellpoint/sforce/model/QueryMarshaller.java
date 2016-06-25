package com.nowellpoint.sforce.model;

import java.io.IOException;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QueryMarshaller implements DynamoDBMarshaller<List<Query>> {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String marshall(List<Query> results) {
		try {
            return objectMapper.writeValueAsString(results);
        } catch (JsonProcessingException e) {
        	e.printStackTrace();
        	return null;
        }
	}

	@Override
	public List<Query> unmarshall(Class<List<Query>> type, String json) {
		try {
			return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Query.class));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}