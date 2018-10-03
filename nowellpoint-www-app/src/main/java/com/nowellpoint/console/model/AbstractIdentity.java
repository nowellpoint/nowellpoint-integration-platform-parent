package com.nowellpoint.console.model;

import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nowellpoint.www.app.util.Path;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Identity.class)
@JsonDeserialize(as = Identity.class)
public abstract class AbstractIdentity extends AbstractResource {
	public abstract String getEmail();
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract String getSubject();
	public abstract String getStatus();
	public abstract OrganizationInfo getOrganization();
	
	@Value.Derived
	public String getName() {
		return getFirstName() != null ? getFirstName().concat(" ").concat(getLastName()) : getLastName(); 
	}
	
	@Value.Default
	public Meta getMeta() {
		return Meta.builder()
				.id(getId())
				.resourcePath(Path.Resource.IDENTITIES)
				.build();
	}
	
	@Value.Default
	public Boolean getActive() {
		return Boolean.FALSE;
	}
	
	@Value.Default
	public Locale getLocale() {
		return Locale.getDefault();
	}
	
	@Value.Default
	public String getTimeZone() {
		return TimeZone.getDefault().getID();
	}
	
	@Value.Default
	public String getUsername() {
		return getEmail();
	}
	
	@Value.Default
	public Photos getPhotos() {
		return Photos.builder().build();
	}
	
	public static Identity of(com.nowellpoint.console.entity.Identity entity) {
		return entity == null ? null : Identity.builder()
				.id(entity.getId().toString())
				.createdBy(UserInfo.of(entity.getCreatedBy()))
				.createdOn(entity.getCreatedOn())
				.email(entity.getEmail())
				.firstName(entity.getFirstName())
				.lastName(entity.getLastName())
				.lastUpdatedBy(UserInfo.of(entity.getLastUpdatedBy()))
				.lastUpdatedOn(entity.getLastUpdatedOn())
				.locale(entity.getLocale())
				.timeZone(entity.getTimeZone())
				.subject(entity.getSubject())
				.status(entity.getStatus())
				.organization(OrganizationInfo.of(entity.getOrganization()))
				.photos(Photos.of(entity.getPhotos()))
				.build();
	}
}