package com.nowellpoint.console.model;

import java.util.Date;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Notification.class)
@JsonDeserialize(as = Notification.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractNotification {
	public abstract String getSubject();
	public abstract String getBody();
	public abstract Date getReceivedOn();
	
	@Value.Default
	public Boolean getIsRead() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Boolean getIsUrgent() {
		return Boolean.FALSE;
	}
	
	public static Notification of(com.nowellpoint.console.entity.Notification source) {
		return source == null ? null : Notification.builder()
				.body(source.getBody())
				.isRead(source.getIsRead())
				.isUrgent(source.getIsUrgent())
				.receivedOn(source.getReceivedOn())
				.subject(source.getSubject())
				.build();
	}
}