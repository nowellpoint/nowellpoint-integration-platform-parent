package com.nowellpoint.console.model;

import java.util.Date;

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
	public abstract @Nullable String getInstanceUrl();
	public abstract @Nullable String getEncryptedToken();
	public abstract @Nullable String getConnectedUser();
	public abstract @Nullable Date getConnectedAt();
	public abstract Subscription getSubscription();
	
	public static final String CONNECTED = "Connected";
	public static final String NOT_CONNECTED = "Not Connected";
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourcePath(Path.Resource.ORANIZATIONS)
				.build();
	} 
	
	@Value.Default
	public String getConnectedStatus() {
		return NOT_CONNECTED;
	}
	
	public static Organization of(com.nowellpoint.console.entity.Organization entity) {
		return entity == null ? null : Organization.builder()
				.id(entity.getId().toString())
				.connectedAt(entity.getConnectedAt())
				.connectedUser(entity.getConnectedUser())
				.connectedStatus(entity.getConnectedStatus())
				.createdBy(UserInfo.of(entity.getCreatedBy()))
				.createdOn(entity.getCreatedOn())
				.domain(entity.getDomain())
				.lastUpdatedBy(UserInfo.of(entity.getLastUpdatedBy()))
				.lastUpdatedOn(entity.getLastUpdatedOn())
				.instanceUrl(entity.getInstanceUrl())
				.encryptedToken(entity.getEncryptedToken())
				.name(entity.getName())
				.number(entity.getNumber())
				.subscription(Subscription.of(entity.getSubscription()))
				.build();
	}
}