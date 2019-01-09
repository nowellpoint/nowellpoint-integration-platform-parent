package com.nowellpoint.console.model;

import java.util.Date;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.entity.StreamingEvent;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = FeedItem.class)
public abstract class AbstractFeedItem {
	public abstract String getId();
	public abstract String getSubject();
	public abstract String getBody();
	public abstract String getType();
	public abstract String getEvent();
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	public abstract Date getCreatedOn();
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	public abstract Date getLastUpdatedOn();
	
	private static final String SUBJECT = "%s %s";
	private static final String BODY = "%s was %s by %s";
	
	public static FeedItem of(com.nowellpoint.console.entity.StreamingEvent entity) {
		return entity == null ? null : FeedItem.builder()
				.body(String.format(BODY, 
						entity.getPayload().getName(), 
						entity.getType(), 
						entity.getPayload().getLastModifiedById()))
				.createdOn(entity.getPayload().getCreatedDate())
				.event(entity.getType())
				.id(entity.getId().toString())
				.lastUpdatedOn(entity.getPayload().getLastModifiedDate())
				.subject(String.format(SUBJECT, 
						entity.getSource(), 
						entity.getType()))
				.type(StreamingEvent.class.getSimpleName())
				.build();
	}
}