package com.nowellpoint.sforce.model;

import java.io.IOException;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NotificationTypeConverter implements DynamoDBTypeConverter<String, List<Notification>> {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convert(List<Notification> notifications) {
		try {
            return objectMapper.writeValueAsString(notifications);
        } catch (JsonProcessingException e) {
        	e.printStackTrace();
        	return null;
        }
	}

	@Override
	public List<Notification> unconvert(String json) {
		try {
			return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Notification.class));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}