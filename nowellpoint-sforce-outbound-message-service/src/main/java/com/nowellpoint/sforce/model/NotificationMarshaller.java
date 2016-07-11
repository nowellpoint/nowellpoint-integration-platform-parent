package com.nowellpoint.sforce.model;

import java.io.IOException;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NotificationMarshaller implements DynamoDBMarshaller<List<Notification>> {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String marshall(List<Notification> notifications) {
		try {
            return objectMapper.writeValueAsString(notifications);
        } catch (JsonProcessingException e) {
        	e.printStackTrace();
        	return null;
        }
	}

	@Override
	public List<Notification> unmarshall(Class<List<Notification>> type, String json) {
		try {
			return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Notification.class));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}