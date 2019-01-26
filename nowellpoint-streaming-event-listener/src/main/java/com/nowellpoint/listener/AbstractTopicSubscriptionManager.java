package com.nowellpoint.listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	
	protected void remove(String key) {
		TOPIC_SUBSCRIPTIONS.remove(key);
	}
	
	protected void replace(String key, TopicSubscription topicSubscription) {
		TOPIC_SUBSCRIPTIONS.replace(key, topicSubscription);
	}
}