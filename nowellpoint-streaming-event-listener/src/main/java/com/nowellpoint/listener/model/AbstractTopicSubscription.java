package com.nowellpoint.listener.model;

import java.util.Optional;

import org.bson.Document;

import com.nowellpoint.listener.connection.MongoConnection;

public abstract class AbstractTopicSubscription {

	private static final String NOTIFICATIONS = "notifications";
	private static final String CHANGE_EVENTS = "change.events";
	
	protected void writeNotification(Document notification) {
		MongoConnection.getInstance().getDatabase().getCollection(NOTIFICATIONS).insertOne(notification);
	}
	
	protected Long getReplayId(String organizationId) {
		Optional<ChangeEvent> document = Optional.ofNullable(MongoConnection.getInstance().getDatabase()
				.getCollection(CHANGE_EVENTS, ChangeEvent.class)
				.find(new Document("organizationId", organizationId))
				.sort(new Document("_id", -1))
				.first());
				
		if (document.isPresent()) {
			return document.get().getEvent().getReplayId();
		} else {
			return Long.valueOf(-1);
		}
	}
}