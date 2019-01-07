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
	
	private static final String SUBJECT = "%s %s %s: %s";
	
	public static FeedItem of(com.nowellpoint.console.entity.StreamingEvent source) {
		return source == null ? null : FeedItem.builder()
				.body(source.getSource().concat(" was updated at ").concat(source.getPayload().getLastModifiedDate().toString()).concat(" by ").concat(source.getPayload().getLastModifiedById()))
				.createdOn(source.getPayload().getCreatedDate())
				.event(source.getType())
				.id(source.getId().toString())
				.lastUpdatedOn(source.getPayload().getLastModifiedDate())
				.subject(String.format(SUBJECT, 
						source.getPayload().getLastModifiedById(), 
						source.getType(), 
						source.getSource(), 
						source.getPayload().getName()))
				.type(StreamingEvent.class.getSimpleName())
				.build();
	}
}