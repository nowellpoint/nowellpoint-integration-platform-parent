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
@JsonSerialize(as = StreamingEventListener.class)
@JsonDeserialize(as = StreamingEventListener.class)
public abstract class AbstractStreamingEventListener {
	public abstract @Nullable String getId();
	public abstract String getPrefix();
	public abstract String getSource();
	public abstract @Nullable String getName();
	public abstract Boolean getEnabled();
	public abstract String getDescription();
	public abstract @Nullable Date getLastEventReceivedOn();
	public abstract @Nullable Long getReplyId();
	
	@Value.Derived
	@JsonIgnore
	public String getHref() {
		return Path.Route.ORGANIZATION_EVENT_LISTENER_SETUP.replace(":sobject", getSource());
	}
	
	public static StreamingEventListener of(com.nowellpoint.console.entity.StreamingEventListener source) {
		return source == null ? null : StreamingEventListener.builder()
				.id(source.getId())
				.enabled(source.getEnabled())
				.description(source.getDescription())
				.lastEventReceivedOn(source.getLastEventReceivedOn())
				.source(source.getSource())
				.prefix(source.getPrefix())
				.replyId(source.getReplayId())
				.build();
	}
}