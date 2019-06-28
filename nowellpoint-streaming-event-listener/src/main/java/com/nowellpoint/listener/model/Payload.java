package com.nowellpoint.listener.model;

import java.util.Date;
import java.util.Map;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class Payload {
	private Date lastModifiedDate;
	private ChangeEventHeader changeEventHeader;
	private Map<String, Object> attributes;
	
	@BsonCreator
	public Payload(
			@BsonProperty("lastModifiedDate") Date lastModifiedDate,
			@BsonProperty("changeEventHeader") ChangeEventHeader changeEventHeader,
			@BsonProperty("") Map<String, Object> attributes) {
		
		this.lastModifiedDate = lastModifiedDate;
		this.changeEventHeader = changeEventHeader;
		this.attributes = attributes;
	}
}