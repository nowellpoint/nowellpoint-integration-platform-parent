package com.nowellpoint.listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nowellpoint.listener.model.TopicConfiguration;
import com.nowellpoint.listener.model.TopicSubscription;

public abstract class Cache {
	
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
	
	protected void connect(String key) {
		if (contains(key)) {
			get(key).connect();
		}
	}
	
	protected void disconnect(String key) {
		if (contains(key)) {
			get(key).disconnect();
		}
	}
	
	protected void reconnect(String key, TopicConfiguration configuration) {
		if (contains(key)) {
			get(key).reconnect(configuration);
		}
	}
	
	protected void remove(String key) {
		TOPIC_SUBSCRIPTIONS.remove(key);
	}
	
	private Boolean contains(String key) {
		return TOPIC_SUBSCRIPTIONS.containsKey(key);
	}
	
	private TopicSubscription get(String key) {
		return TOPIC_SUBSCRIPTIONS.get(key);
	}
}