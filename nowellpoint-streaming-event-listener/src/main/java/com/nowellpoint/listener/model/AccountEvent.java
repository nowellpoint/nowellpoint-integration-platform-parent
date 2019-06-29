package com.nowellpoint.listener.model;

import java.util.Map;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccountEvent {
	private String changeType;
	private Long timestamp;
	private String userId;
	private String transactionKey;
	private Map<String, Object> payload;
	
	private @BsonIgnore String accountId;
	private @BsonIgnore String organizationId;
	
	@BsonCreator
	public AccountEvent(@BsonProperty("changeType") String changeType,
			@BsonProperty("timestamp") Long timestamp,
			@BsonProperty("userId") String userId,
			@BsonProperty("transactionKey") String transactionKey,
			@BsonProperty("payload") Map<String, Object> payload) {
		
		this.changeType = changeType;
		this.timestamp = timestamp;
		this.userId = userId;
		this.transactionKey = transactionKey;
		this.payload = payload;
	}
}