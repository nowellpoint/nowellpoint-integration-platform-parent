package com.nowellpoint.console.model;

import java.util.Date;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = EventListener.class)
@JsonDeserialize(as = EventListener.class)
public abstract class AbstractEventListener {
	public abstract String getId();
	public abstract String getName();
	public abstract Boolean getEnabled();
	public abstract String getDescription();
	public abstract Date getLastEventReceivedOn();
	public abstract Long getReplyId();
	
	public static EventListener of(com.nowellpoint.console.entity.EventListener source) {
		return source == null ? null : EventListener.builder()
				.id(source.getId())
				.name(source.getName())
				.enabled(source.getEnabled())
				.description(source.getDescription())
				.lastEventReceivedOn(source.getLastEventReceivedOn())
				.replyId(source.getReplayId())
				.build();
	}
}