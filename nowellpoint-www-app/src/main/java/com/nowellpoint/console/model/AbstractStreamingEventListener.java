package com.nowellpoint.console.model;

import java.util.Date;
import java.util.Objects;

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
	
	@Value.Default
	public Long getReplyId() {
		return Long.valueOf(-1);
	}
	
	@Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StreamingEventListener listener = (StreamingEventListener) o;
        return Objects.equals(getPrefix(), listener.getPrefix());
    }
	
	@Override
	public int hashCode() {
       return Objects.hash(getPrefix());
    }
	
	public static StreamingEventListener of(com.nowellpoint.console.entity.StreamingEventListener source) {
		System.out.println(source.getReplayId());
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