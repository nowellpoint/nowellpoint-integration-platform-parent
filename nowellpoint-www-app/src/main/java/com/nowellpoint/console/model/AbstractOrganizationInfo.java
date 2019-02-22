package com.nowellpoint.console.model;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.util.Path;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new", depluralize = true, depluralizeDictionary = {"notification:notifications"})
@JsonSerialize(as = OrganizationInfo.class)
@JsonDeserialize(as = OrganizationInfo.class)
public abstract class AbstractOrganizationInfo {
	public abstract String getId();
	public abstract @Nullable String getNumber();
	public abstract @Nullable String getDomain();
	public abstract @Nullable String getPlan();
	public abstract @Nullable String getName();
	public abstract @Nullable List<Notification> getNotifications();
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourcePath(Path.Resource.ORANIZATIONS)
				.build();
	}
	
	public static OrganizationInfo of(com.nowellpoint.console.entity.Organization source) {
		return source == null ? null : OrganizationInfo.builder()
				.domain(source.getDomain())
				.id(source.getId().toString())
				.name(source.getName())
				.number(source.getNumber())
				.plan(source.getSubscription().getPlanName())
				.notifications(Notifications.of(source.getNotifications()))
				.build();
	}
}