package com.nowellpoint.console.model;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.util.Path;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Organization.class)
@JsonDeserialize(as = Organization.class)
public abstract class AbstractOrganization extends AbstractResource {
	public abstract String getNumber();
	public abstract String getDomain();
	public abstract String getName();
	public abstract @Nullable Connection getConnection();
	public abstract Subscription getSubscription();
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourcePath(Path.Resource.ORANIZATIONS)
				.build();
	} 
	
	public static Organization of(com.nowellpoint.console.entity.Organization entity) {
		return entity == null ? null : Organization.builder()
				.id(entity.getId().toString())
				.createdBy(UserInfo.of(entity.getCreatedBy()))
				.createdOn(entity.getCreatedOn())
				.domain(entity.getDomain())
				.lastUpdatedBy(UserInfo.of(entity.getLastUpdatedBy()))
				.lastUpdatedOn(entity.getLastUpdatedOn())
				.name(entity.getName())
				.number(entity.getNumber())
				.connection(Connection.of(entity.getConnection()))
				.subscription(Subscription.of(entity.getSubscription()))
				.build();
	}
}