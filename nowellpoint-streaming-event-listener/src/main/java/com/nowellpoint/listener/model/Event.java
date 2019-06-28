package com.nowellpoint.listener.model;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class Event {
	private Long replayId;
	
	@BsonCreator
	public Event(@BsonProperty("replayId") Long replayId) {
		this.replayId = replayId;
	}
}