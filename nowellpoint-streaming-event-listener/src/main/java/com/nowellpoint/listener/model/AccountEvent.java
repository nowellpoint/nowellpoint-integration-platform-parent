package com.nowellpoint.listener.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Getter;

@Builder
public class AccountEvent {
	@Getter private String type;
	@Getter private Long timestamp;
	@Getter private String userId;
	@Getter private String transactionKey;
	@Getter private AccountPayload payload;
	
	@BsonCreator
	public AccountEvent(@BsonProperty("type") String type,
			@BsonProperty("timestamp") Long timestamp,
			@BsonProperty("userId") String userId,
			@BsonProperty("transactionKey") String transactionKey,
			@BsonProperty("payload") AccountPayload payload) {
		
		this.type = type;
		this.timestamp = timestamp;
		this.userId = userId;
		this.transactionKey = transactionKey;
		this.payload = payload;
	}
}