package com.nowellpoint.listener.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nowellpoint.listener.model.TopicSubscription;

public class CacheManager {

	private static final Map<String,String> TOKEN_CACHE = new ConcurrentHashMap<>();
	private static final Map<String,TopicSubscription> TOPIC_SUBSCRIPTIONS = new ConcurrentHashMap<>();
	
	public static Map<String,String> getTokenCache() {
		return TOKEN_CACHE;
	}
	
	public static Map<String,TopicSubscription> getTopicSubscriptionCache() {
		return TOPIC_SUBSCRIPTIONS;
	}
}