package com.nowellpoint.console.model;

import java.util.Date;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.util.Path;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Notification.class)
@JsonDeserialize(as = Notification.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractNotification extends AbstractResource {
	public abstract String getSubject();
	public abstract String getMessage();
	public abstract String getReceivedFrom();
	public abstract Date getReceivedOn();

	
	@Value.Default
	public Boolean getIsRead() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getIsUrgent() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourcePath(Path.Resource.ORANIZATIONS)
				.build();
	}
	
	public static Notification of(com.nowellpoint.console.entity.Notification source) {
		return source == null ? null : Notification.builder()
				.id(source.getId().toString())
				.isRead(source.getIsRead())
				.isUrgent(source.getIsUrgent())
				.message(source.getMessage())
				.receivedOn(source.getReceivedOn())
				.subject(source.getSubject())
				.receivedFrom(source.getReceivedFrom())
				.build();
	}
}