package com.nowellpoint.console.model;

import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.util.Path;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = EventListener.class)
@JsonDeserialize(as = EventListener.class)
public abstract class AbstractEventListener {
	public abstract @Nullable String getId();
	public abstract String getPrefix();
	public abstract String getSObject();
	public abstract @Nullable String getName();
	public abstract Boolean getEnabled();
	public abstract String getDescription();
	public abstract @Nullable Date getLastEventReceivedOn();
	public abstract @Nullable Long getReplyId();
	
	@Value.Derived
	@JsonIgnore
	public String getHref() {
		return Path.Route.ORGANIZATION_EVENT_LISTENER_SETUP.replace(":sobject", getSObject());
	}
	
	public static EventListener of(com.nowellpoint.console.entity.StreamingEventListener source) {
		return source == null ? null : EventListener.builder()
				.id(source.getId())
				.enabled(source.getEnabled())
				.description(source.getDescription())
				.lastEventReceivedOn(source.getLastEventReceivedOn())
				.sObject(source.getSObject())
				.prefix(source.getPrefix())
				.replyId(source.getReplayId())
				.build();
	}
}