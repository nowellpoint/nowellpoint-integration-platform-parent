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

public abstract class AbstractTopicSubscriptionManager {
	
	private static final Map<String,TopicSubscription> TOPIC_SUBSCRIPTIONS = new ConcurrentHashMap<>();
	private static final Jsonb jsonb = JsonbBuilder.create(getJsonbConfig());
		
	protected void put(String key, TopicSubscription topicSubscription) {
		TOPIC_SUBSCRIPTIONS.put(key, topicSubscription);
	}
	
	protected void disconnectAll() {
		TOPIC_SUBSCRIPTIONS.keySet().stream().forEach(k -> {
			TOPIC_SUBSCRIPTIONS.get(k).disconnect();
		});
		TOPIC_SUBSCRIPTIONS.clear();
	}
	
	protected TopicSubscription get(String key) {
		return TOPIC_SUBSCRIPTIONS.get(key);
	}
	
	protected void connect(String key) {
		get(key).connect();
	}
	
	protected void subscribe(String key) {
		get(key).subscribe();
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
	
	protected void replace(String key, TopicSubscription topicSubscription) {
		TOPIC_SUBSCRIPTIONS.replace(key, topicSubscription);
	}
	
	protected TopicConfiguration readConfiguration(S3Object s3object) {
		return jsonb.fromJson(s3object.getObjectContent(), TopicConfiguration.class);
	}
	
	protected S3Event readEvent(TextMessage message) throws JsonbException, JMSException {
		return jsonb.fromJson(message.getText(), S3Event.class);
	}
	
	private static JsonbConfig getJsonbConfig() {
		return new JsonbConfig().withNullValues(Boolean.TRUE).withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {

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