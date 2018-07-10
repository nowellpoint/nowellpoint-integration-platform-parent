package com.nowellpoint.console.model;

import java.time.Instant;
import java.util.Date;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.console.api.UserProfileResource;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly = true, create = "new")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(as = UserProfile.class)
@JsonDeserialize(as = UserProfile.class)
public abstract class AbstractUserProfile {
	public abstract String getId();
	public abstract String getUsername();
	public abstract String getName();
	public abstract String getLastName();
	public abstract String getFirstName();
	public abstract String getTitle();
	public abstract String getEmail();
	public abstract String getPhone();
	public abstract Boolean getIsActive();
	public abstract Address getAddress();
	public abstract Preferences getPreferences();
	public abstract OrganizationInfo getOrganization();
	public abstract Photos getPhotos();
	
	private final Date now = Date.from(Instant.now());
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourceClass(UserProfileResource.class)
				.build();
	}
	
	@Value.Default
	public Date getCreatedOn() {
		return now;
	}
	
	@Value.Default
	public Date getLastUpdatedOn() {
		return now;
	}
	
	public static UserProfile of(com.nowellpoint.console.entity.UserProfile source) {
		return UserProfile.builder()
				.address(Address.of(source.getAddress()))
				.createdOn(source.getCreatedOn())
				.email(source.getEmail())
				.firstName(source.getFirstName())
				.id(source.getId().toString())
				.isActive(source.getIsActive())
				.lastName(source.getName())
				.lastUpdatedOn(source.getLastUpdatedOn())
				.name(source.getName())
				.organization(OrganizationInfo.of(source.getOrganization()))
				.phone(source.getPhone())
				.photos(Photos.of(source.getPhotos()))
				.preferences(Preferences.of(source.getPreferences()))
				.title(source.getTitle())
				.username(source.getUsername())
				.build();
	}
}