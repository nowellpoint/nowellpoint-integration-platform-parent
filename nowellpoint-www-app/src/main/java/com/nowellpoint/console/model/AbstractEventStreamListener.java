package com.nowellpoint.console.model;

import java.util.Date;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.util.Path;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, get = {"is*", "get*"})
@JsonSerialize(as = EventStreamListener.class)
@JsonDeserialize(as = EventStreamListener.class)
public abstract class AbstractEventStreamListener {
	public abstract @Nullable String getTopicId();
	public abstract @Nullable String getApiVersion();
	public abstract String getPrefix();
	public abstract String getSource();
	public abstract String getName();
	public abstract String getDescription();
	public abstract @Nullable UserInfo getCreatedBy();
	public abstract @Nullable UserInfo getLastUpdatedBy();
	public abstract @Nullable @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getCreatedOn();
	public abstract @Nullable @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getLastUpdatedOn();
	public abstract @Nullable @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getStartedOn();
	public abstract @Nullable @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") Date getStoppedOn();
	public abstract @Nullable Meta getMeta();
	
	private static final String QUERY = "SELECT Id, Name, CreatedById, CreatedDate, LastModifiedById, LastModifiedDate FROM %s";
	
	@Value.Derived
	@JsonIgnore
	public String getHref() {
		return Path.Route.EVENT_STREAM_VIEW.replace(":source", getSource());
	}
	
	@Value.Derived
	public String getChannel() {
		return "/topic/".concat(getName());
	}
	
	@Value.Default
	public Boolean getNotifyForOperationCreate() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getNotifyForOperationUpdate() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getNotifyForOperationUndelete() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getNotifyForOperationDelete() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getActive() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getCustom() {
		return Boolean.FALSE;
	}
	
	@Value.Derived
	public String getQuery() {
		return String.format(QUERY, getSource());
	}
	
	@Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EventStreamListener listener = (EventStreamListener) o;
        return Objects.equals(getPrefix(), listener.getPrefix());
    }
	
	@Override
	public int hashCode() {
       return Objects.hash(getPrefix());
    }
	
	public static EventStreamListener of(com.nowellpoint.console.entity.EventStreamListener source) {
		return source == null ? null : EventStreamListener.builder()
				.active(source.getActive())
				.createdBy(UserInfo.of(source.getCreatedBy()))
				.createdOn(source.getCreatedOn())
				.custom(source.getCustom())
				.description(source.getDescription())
				.lastUpdatedBy(UserInfo.of(source.getLastUpdatedBy()))
				.lastUpdatedOn(source.getLastUpdatedOn())
				.name(source.getName())
				.notifyForOperationCreate(source.getNotifyForOperationCreate())
				.notifyForOperationDelete(source.getNotifyForOperationDelete())
				.notifyForOperationUndelete(source.getNotifyForOperationUndelete())
				.notifyForOperationUpdate(source.getNotifyForOperationUpdate())
				.source(source.getSource())
				.startedOn(source.getStartedOn())
				.stoppedOn(source.getStoppedOn())
				.prefix(source.getPrefix())
				.topicId(source.getTopicId())
				.build();
	}
}