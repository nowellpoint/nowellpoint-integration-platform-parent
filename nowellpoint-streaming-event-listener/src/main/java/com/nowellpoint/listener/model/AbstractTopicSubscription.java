package com.nowellpoint.listener.model;

import java.util.Optional;

import org.bson.Document;
import org.jboss.logging.Logger;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.listener.connection.MongoConnection;
import com.nowellpoint.util.SecretsManager;

public abstract class AbstractTopicSubscription {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractTopicSubscription.class);

	private static final String NOTIFICATIONS = "notifications";
	private static final String CHANGE_EVENTS = "change.events";
	private static final String STREAMING_EVENTS = "streaming.events";

	protected Token refreshToken(String refreshToken) {
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
				.setRefreshToken(refreshToken)
				.build();
		
		OauthAuthenticationResponse response = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		return response.getToken();
    }
	
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
	
	protected void writeStreamingEvent(Document streamingEvent) {
		try {
			MongoConnection.getInstance().getDatabase().getCollection(STREAMING_EVENTS).insertOne(streamingEvent);
		} catch (MongoWriteException e) {
            if (e.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
            	LOGGER.warn(e.getMessage());
            }
		}
	}
}