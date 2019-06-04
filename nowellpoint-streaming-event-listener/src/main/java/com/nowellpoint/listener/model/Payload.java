package com.nowellpoint.listener.model;

import java.util.Date;
import java.util.Map;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Getter;

@Builder
public class Payload {
	@Getter private Date lastModifiedDate;
	@Getter private ChangeEventHeader changeEventHeader;
	//@Getter private Map<String, Object> changedValues;
	
	@BsonCreator
	public Payload(
			@BsonProperty("lastModifiedDate") Date lastModifiedDate,
			@BsonProperty("changeEventHeader") ChangeEventHeader changeEventHeader) {
		
		this.lastModifiedDate = lastModifiedDate;
		this.changeEventHeader = changeEventHeader;
	}
}