package com.nowellpoint.console.model;

import java.time.Instant;
import java.util.Date;

import org.bson.types.ObjectId;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.api.OrganizationResource;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Organization.class)
@JsonDeserialize(as = Organization.class)
public abstract class AbstractOrganization {
	public abstract String getNumber();
	public abstract String getDomain();
	public abstract String getName();
	public abstract Subscription getSubscription();
	
	private Date now = Date.from(Instant.now());
	
	@Value.Default
	public Date getCreatedOn() {
		return now;
	}
	
	@Value.Default
	public Date getLastUpdatedOn() {
		return now;
	}
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(OrganizationResource.class)
				.build();
	}
	
	@Value.Default
	public String getId() {
		return new ObjectId().toString();
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