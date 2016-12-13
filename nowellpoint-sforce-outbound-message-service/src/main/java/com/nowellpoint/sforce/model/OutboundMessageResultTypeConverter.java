package com.nowellpoint.sforce.model;

import java.io.IOException;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OutboundMessageResultTypeConverter implements DynamoDBTypeConverter<String, List<OutboundMessageResult>> {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convert(List<OutboundMessageResult> results) {
		try {
            return objectMapper.writeValueAsString(results);
        } catch (JsonProcessingException e) {
        	e.printStackTrace();
        	return null;
        }
	}

	@Override
	public List<OutboundMessageResult> unconvert(String json) {
		try {
			return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, OutboundMessageResult.class));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}