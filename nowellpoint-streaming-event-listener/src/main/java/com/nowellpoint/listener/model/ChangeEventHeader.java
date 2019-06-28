package com.nowellpoint.listener.model;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class ChangeEventHeader {	
	private Long commitNumber;
	private String commitUser;
	private Integer sequenceNumber;
	private String entityName;
	private String changeType;
	private String changeOrigin;
	private String transactionKey;
	private Long commitTimestamp;
	private List<String> recordIds;
	
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