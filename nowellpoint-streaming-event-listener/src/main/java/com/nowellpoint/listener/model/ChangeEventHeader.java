package com.nowellpoint.listener.model;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Getter;

@Builder
public class ChangeEventHeader {	
	@Getter private Long commitNumber;
	@Getter private String commitUser;
	@Getter private Integer sequenceNumber;
	@Getter private String entityName;
	@Getter private String changeType;
	@Getter private String changeOrigin;
	@Getter private String transactionKey;
	@Getter private Long commitTimestamp;
	@Getter private List<String> recordIds;
	
	@BsonCreator
	public ChangeEventHeader(
			@BsonProperty("commitNumber") Long commitNumber,
			@BsonProperty("commitUser") String commitUser,
			@BsonProperty("sequenceNumber") Integer sequenceNumber,
			@BsonProperty("entityName") String entityName,
			@BsonProperty("changeType") String changeType,
			@BsonProperty("changeOrigin") String changeOrigin,
			@BsonProperty("transactionKey") String transactionKey,
			@BsonProperty("commitTimestamp") Long commitTimestamp,
			@BsonProperty("recordIds") List<String> recordIds) {
		
		this.commitNumber = commitNumber;
		this.commitUser = commitUser;
		this.sequenceNumber = sequenceNumber;
		this.entityName = entityName;
		this.changeType = changeType;
		this.changeOrigin = changeOrigin;
		this.transactionKey = transactionKey;
		this.commitTimestamp = commitTimestamp;
		this.recordIds = recordIds;
	}
}