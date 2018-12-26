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
	public abstract String getName();
	public abstract String getDescription();
	public abstract @Nullable Date getLastEventReceivedOn();
	public abstract @Nullable Long getReplyId();
	
	private static final String API_VERSION = "44.0";
	private static final String QUERY = "SELECT Id, Name, CreatedById, CreatedDate, LastModifiedById, LastModifiedDate FROM %s";
	
	@Value.Derived
	@JsonIgnore
	public String getHref() {
		return Path.Route.ORGANIZATION_STREAMING_EVENTS_SETUP.replace(":source", getSource());
	}
	
	@Value.Default
	public Boolean getEnabled() {
		return Boolean.FALSE;
	}
	
	@Value.Derived
	public String getQuery() {
		return String.format(QUERY, getSource());
	}
	
	@Value.Derived
	public String getApiVersion() {
		return API_VERSION;
	}
	
	public static StreamingEventListener of(com.nowellpoint.console.entity.StreamingEventListener source) {
		return source == null ? null : StreamingEventListener.builder()
				.id(source.getId())
				.enabled(source.getEnabled())
				.description(source.getDescription())
				.lastEventReceivedOn(source.getLastEventReceivedOn())
				.name(source.getName())
				.source(source.getSource())
				.prefix(source.getPrefix())
				.replyId(source.getReplayId())
				.build();
	}
}