package com.nowellpoint.console.model;

import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.api.OrganizationResource;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new", depluralize = true, depluralizeDictionary = {"transaction:transactions"})
@JsonSerialize(as = Organization.class)
@JsonDeserialize(as = Organization.class)
public abstract class AbstractOrganization {
	public abstract Date getCreatedOn();
	public abstract String getId();
	public abstract Date getLastUpdatedOn();
	public abstract String getNumber();
	public abstract String getDomain();
	public abstract @Nullable String getName();
	public abstract Subscription getSubscription();
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(OrganizationResource.class)
				.build();
	}
	
	public static Organization of(com.nowellpoint.console.entity.Organization source) {
		return Organization.builder()
				.createdOn(source.getCreatedOn())
				.domain(source.getDomain())
				.id(source.getId().toString())
				.lastUpdatedOn(source.getLastUpdatedOn())
				.name(source.getName())
				.number(source.getNumber())
				.subscription(Subscription.of(source.getSubscription()))
				.build();
	}
}