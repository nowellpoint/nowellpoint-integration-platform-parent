package com.nowellpoint.listener.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class ChangeEvent {
	private String schema;
	private String organizationId;
	private Payload payload;
	private Event event;
	
	@BsonCreator
	public ChangeEvent(
			@BsonProperty("schema") String schema, 
			@BsonProperty("organizationId") String organizationId,
			@BsonProperty("payload") Payload payload,
			@BsonProperty("event") Event event) {
		
		this.schema = schema;
		this.organizationId = organizationId;
		this.payload = payload;
		this.event = event;
	}
}