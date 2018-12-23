package com.nowellpoint.console.model;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.util.Path;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Organization.class)
@JsonDeserialize(as = Organization.class)
public abstract class AbstractOrganization extends AbstractResource {
	public abstract String getNumber();
	public abstract String getDomain();
	public abstract String getName();
	public abstract @Nullable Address getAddress();
	public abstract @Nullable Connection getConnection();
	public abstract Dashboard getDashboard();
	public abstract Subscription getSubscription();
	public abstract List<StreamingEventListener> getStreamingEventListeners();
	
	@Value.Default
	public String getOrganizationType() {
		return "Not Available";
	}
 	
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
				.organizationType(entity.getOrganizationType())
				.address(Address.of(entity.getAddress()))
				.connection(Connection.of(entity.getConnection()))
				.dashboard(Dashboard.of(entity.getDashboard()))
				.subscription(Subscription.of(entity.getSubscription()))
				.streamingEventListeners(StreamingEventListeners.of(entity.getStreamingEventListeners()))
				.build();
	}
}