package com.nowellpoint.listener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.config.PropertyVisibilityStrategy;

import com.amazonaws.services.s3.model.S3Object;
import com.nowellpoint.listener.model.S3Event;
import com.nowellpoint.listener.model.TopicConfiguration;

public abstract class TopicSubscriptionCache {
	
	private static final Map<String,TopicSubscription> TOPIC_SUBSCRIPTIONS = new ConcurrentHashMap<>();
	private static final Jsonb JSON_BUILDER = JsonbBuilder.create(getJsonbConfig());
		
	protected void put(String key, TopicSubscription topicSubscription) {
		TOPIC_SUBSCRIPTIONS.put(key, topicSubscription);
	}
	
	protected void disconnectAll() {
		TOPIC_SUBSCRIPTIONS.keySet().stream().forEach(k -> {
			TOPIC_SUBSCRIPTIONS.get(k).disconnect();
		});
		TOPIC_SUBSCRIPTIONS.clear();
	}
	
	protected void connect(String key) {
		get(key).connect();
	}
	
	protected void disconnect(String key) {
		get(key).disconnect();
	}
	
	protected void reconnect(String key, TopicConfiguration configuration) {
		get(key).reconnect(configuration);
	}
	
	protected void remove(String key) {
		TOPIC_SUBSCRIPTIONS.remove(key);
	}
	
	protected TopicConfiguration readConfiguration(S3Object s3object) {
		return JSON_BUILDER.fromJson(s3object.getObjectContent(), TopicConfiguration.class);
	}
	
	protected S3Event readEvent(TextMessage message) throws JsonbException, JMSException {
		return JSON_BUILDER.fromJson(message.getText(), S3Event.class);
	}
	
	private TopicSubscription get(String key) {
		return TOPIC_SUBSCRIPTIONS.get(key);
	}
	
	private static JsonbConfig getJsonbConfig() {
		return new JsonbConfig()
				.withNullValues(Boolean.TRUE)
				.withPropertyVisibilityStrategy(
						new PropertyVisibilityStrategy() {
							
							@Override
							public boolean isVisible(Field field) {
								return true;
							}
							
							@Override
							public boolean isVisible(Method method) {
								return false;
							}
							
						});
	}
}