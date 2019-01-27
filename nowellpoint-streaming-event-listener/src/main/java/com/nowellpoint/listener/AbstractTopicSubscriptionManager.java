package com.nowellpoint.listener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;

import com.amazonaws.services.s3.model.S3Object;
import com.nowellpoint.listener.model.TopicConfiguration;

public abstract class AbstractTopicSubscriptionManager {
	
	private static final Map<String,TopicSubscription> TOPIC_SUBSCRIPTIONS = new ConcurrentHashMap<>();
		
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
		JsonbConfig config = new JsonbConfig().withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {

			@Override
			public boolean isVisible(Field field) {
				return true;
			}

			@Override
			public boolean isVisible(Method method) {
				return false;
			}
			
		});
		
		return JsonbBuilder.create(config).fromJson(s3object.getObjectContent(), TopicConfiguration.class);
	}
}