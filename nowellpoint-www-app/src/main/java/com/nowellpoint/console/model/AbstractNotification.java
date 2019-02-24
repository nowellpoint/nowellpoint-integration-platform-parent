package com.nowellpoint.console.model;

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
	public abstract String getWho();

	
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
				.createdBy(UserInfo.of(source.getCreatedBy()))
				.createdOn(source.getCreatedOn())
				.id(source.getId().toString())
				.isRead(source.getIsRead())
				.isUrgent(source.getIsUrgent())
				.lastUpdatedBy(UserInfo.of(source.getLastUpdatedBy()))
				.lastUpdatedOn(source.getLastUpdatedOn())
				.message(source.getMessage())
				.subject(source.getSubject())
				.build();
	}
}